package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toAuthFirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthUserRemoteDataModel
import com.msoula.hobbymatchmaker.core.authentication.data.models.ProviderTypeDataModel
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.data.FirestoreUsersCollection
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import com.msoula.hobbymatchmaker.core.common.toGenericAppError
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore

class AuthenticationRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authManager: AuthManager
) : AuthenticationRemoteDataSource {

    override suspend fun authenticationSignOut(): AppResult<Unit, AppError> = authManager.signOut()

    override suspend fun linkWithCredential(credential: AuthCredential): AppResult<AuthUserRemoteDataModel?, AppError> {
        val currentUser = auth.currentUser ?: return AppResult.Failure(AppError.Domain.Unauthorized)

        return safeFirebaseCall {
            currentUser.linkWithCredential(credential).user?.toAuthFirebaseUser()
        }
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): AppResult<String, AppError> =
        safeFirebaseCall {
            val user = auth.createUserWithEmailAndPassword(email, password).user
            user?.uid ?: throw IllegalStateException("UID missing after sign-up")
        }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AppResult<String, AppError> =
        safeFirebaseCall {
            val user = auth.signInWithEmailAndPassword(email, password).user
            user?.uid ?: throw IllegalStateException("UID missing after sign-in")
        }

    override suspend fun resetPassword(email: String): AppResult<Unit, AppError> =
        safeFirebaseCall {
            auth.sendPasswordResetEmail(email)
        }

    override suspend fun isFirstSignIn(uid: String): AppResult<Boolean, AppError> =
        if (uid.isBlank()) AppResult.Success(true) else safeFirebaseCall {
            val snapshot = firestore
                .collection(FirestoreUsersCollection)
                .document(uid)
                .get()

            !snapshot.exists
        }

    override suspend fun fetchFirebaseUserInfo(): AppResult<AuthUserRemoteDataModel?, AppError> =
        safeFirebaseCall {
            auth.currentUser?.toAuthFirebaseUser()
        }

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
