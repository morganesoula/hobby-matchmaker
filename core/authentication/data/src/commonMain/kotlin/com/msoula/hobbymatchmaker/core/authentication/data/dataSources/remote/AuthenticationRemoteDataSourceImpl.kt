package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toAuthFirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthFirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore

class AuthenticationRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authManager: AuthManager
) : AuthenticationRemoteDataSource {

    override suspend fun authenticationSignOut(): AppResult<Unit, AppError> = authManager.signOut()

    override suspend fun signInWithCredentials(
        credential: AuthCredential,
        providerType: ProviderType
    ): AppResult<AuthFirebaseUser?, AppError> = authManager.signIn(providerType, credential)

    override suspend fun linkWithCredential(credential: AuthCredential): AppResult<AuthFirebaseUser?, AppError> =
        if (auth.currentUser == null) {
            AppResult.Failure(AppError.Domain.Unauthorized)
        } else {
            safeFirebaseCall {
                auth.currentUser!!.linkWithCredential(credential).user?.toAuthFirebaseUser()
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

    override fun getUserUid(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun isFirstSignIn(uid: String): AppResult<Boolean, AppError> =
        if (uid.isBlank()) AppResult.Success(true) else safeFirebaseCall {
            val snapshot = firestore
                .collection("users")
                .document(uid)
                .get()

            !snapshot.exists
        }

    override suspend fun fetchFirebaseUserInfo(): AppResult<AuthFirebaseUser?, AppError> =
        safeFirebaseCall {
            auth.currentUser?.toAuthFirebaseUser()
        }
}
