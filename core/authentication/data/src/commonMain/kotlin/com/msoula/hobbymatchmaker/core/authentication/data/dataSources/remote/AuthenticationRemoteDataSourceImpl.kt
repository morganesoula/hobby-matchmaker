package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toAuthFirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthUserRemoteDataModel
import com.msoula.hobbymatchmaker.core.authentication.data.models.ProviderTypeDataModel
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.data.FirestoreBlockedCollection
import com.msoula.hobbymatchmaker.core.common.data.FirestoreCircleCollection
import com.msoula.hobbymatchmaker.core.common.data.FirestorePseudosCollection
import com.msoula.hobbymatchmaker.core.common.data.FirestoreSocialInvitesCollection
import com.msoula.hobbymatchmaker.core.common.data.FirestoreUsersCollection
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.core.common.flatMapSuspend
import com.msoula.hobbymatchmaker.core.common.toGenericAppError
import com.msoula.hobbymatchmaker.core.common.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.core.common.safeNetworkCall
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CancellationException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val AUTH_TIME_CLAIM = "auth_time"

// Firebase rejects sensitive operations (account deletion) when the last sign-in is older than ~5 minutes
private const val RECENT_LOGIN_WINDOW_SECONDS = 5 * 60

class AuthenticationRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val authManager: AuthManager,
    private val connectivityChecker: NetworkConnectivityChecker
) : AuthenticationRemoteDataSource {

    override suspend fun authenticationSignOut(): AppResult<Unit, AppError> = authManager.signOut()

    /**
     * Checks the sign-in is recent enough *before* wiping anything, so a re-authentication
     * requirement never leaves the account half-deleted. Then removes the user's whole remote
     * footprint and finally the Firebase Auth user itself.
     */
    override suspend fun deleteAccount(): AppResult<Unit, AppError> {
        val currentUser = auth.currentUser ?: return AppResult.Failure(AppError.Domain.Unauthorized)

        return safeNetworkCall(connectivityChecker) {
            currentUser.getIdTokenResult(false).claims[AUTH_TIME_CLAIM]
        }
            .flatMap { authTime ->
                if (isRecentLogin(authTime)) AppResult.Success(Unit)
                else AppResult.Failure(AppError.Authentication.RecentLoginRequired)
            }
            .flatMapSuspend {
                safeNetworkCall(connectivityChecker) { deleteRemoteUserData(currentUser.uid) }
            }
            .flatMapSuspend {
                safeNetworkCall(connectivityChecker) { currentUser.delete() }
            }
    }

    @OptIn(ExperimentalTime::class)
    private fun isRecentLogin(authTimeClaim: Any?): Boolean {
        // Android exposes a Number, iOS an NSNumber: parse from its string form as a fallback
        val authTimeSeconds = (authTimeClaim as? Number)?.toLong()
            ?: authTimeClaim?.toString()?.toDoubleOrNull()?.toLong()
            ?: run {
                // Unknown sign-in time: let Firebase's own check on delete() decide
                Logger.e("deleteAccount: auth_time claim unavailable")
                return true
            }

        return Clock.System.now().epochSeconds - authTimeSeconds <= RECENT_LOGIN_WINDOW_SECONDS
    }

    private suspend fun deleteRemoteUserData(uid: String) {
        val usersCollection = firestore.collection(FirestoreUsersCollection)
        val userRef = usersCollection.document(uid)
        val pseudo = userRef.get().get<String?>("information.pseudo")

        val circleRefs = userRef.collection(FirestoreCircleCollection).get().documents
            .map { it.reference }
        val blockedRefs = userRef.collection(FirestoreBlockedCollection).get().documents
            .map { it.reference }
        val sentInviteRefs = firestore.collection(FirestoreSocialInvitesCollection)
            .where { "fromUid" equalTo uid }
            .get()
            .documents
            .map { it.reference }
        val receivedInviteRefs = pseudo?.let {
            firestore.collection(FirestoreSocialInvitesCollection)
                .where { "toPseudo" equalTo it }
                .get()
                .documents
                .map { document -> document.reference }
        }.orEmpty()

        firestore.batch().apply {
            circleRefs.forEach { memberEntry ->
                // Remove the user from each friend's circle, then the friend from the user's circle
                delete(
                    usersCollection.document(memberEntry.id)
                        .collection(FirestoreCircleCollection)
                        .document(uid)
                )
                delete(memberEntry)
            }
            // Reports filed about or by this user are kept for safety reviews
            (blockedRefs + sentInviteRefs + receivedInviteRefs).forEach { delete(it) }
            // Free the pseudo so it can be picked again
            pseudo?.let { delete(firestore.collection(FirestorePseudosCollection).document(it.lowercase())) }
            delete(userRef)
        }.commit()

        try {
            storage.reference.child("avatars/$uid/avatar.jpg").delete()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // No avatar uploaded (object not found): nothing to delete
            Logger.d("deleteAccount: no remote avatar deleted - ${e.message}")
        }
    }

    override suspend fun linkWithCredential(credential: AuthCredential): AppResult<AuthUserRemoteDataModel?, AppError> {
        val currentUser = auth.currentUser ?: return AppResult.Failure(AppError.Domain.Unauthorized)

        return safeNetworkCall(connectivityChecker) {
            currentUser.linkWithCredential(credential).user?.toAuthFirebaseUser()
        }
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): AppResult<String, AppError> =
        safeNetworkCall(connectivityChecker) {
            val user = auth.createUserWithEmailAndPassword(email, password).user
            user?.uid ?: throw IllegalStateException("UID missing after sign-up")
        }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AppResult<String, AppError> =
        safeNetworkCall(connectivityChecker) {
            val user = auth.signInWithEmailAndPassword(email, password).user
            user?.uid ?: throw IllegalStateException("UID missing after sign-in")
        }

    override suspend fun resetPassword(email: String): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            auth.sendPasswordResetEmail(email)
        }

    override suspend fun isFirstSignIn(uid: String): AppResult<Boolean, AppError> =
        if (uid.isBlank()) AppResult.Success(true)
        else safeNetworkCall(connectivityChecker) {
            val snapshot = firestore
                .collection(FirestoreUsersCollection)
                .document(uid)
                .get()

            !snapshot.exists
        }

    override suspend fun fetchFirebaseUserInfo(): AppResult<AuthUserRemoteDataModel?, AppError> =
        AppResult.Success(auth.currentUser?.toAuthFirebaseUser())

    override suspend fun signInWithSocialProvider(
        providerType: ProviderTypeDataModel,
        credentialProvider: suspend () -> Any?
    ): AppResult<AuthUserRemoteDataModel?, AppError> {
        return try {
            val credential = credentialProvider() as? AuthCredential
                ?: return AppResult.Failure(AppError.Authentication.InvalidCredentials)

            authManager.signIn(providerType, credential)
        } catch (e: Exception) {
            AppResult.Failure(e.toGenericAppError())
        }
    }
}
