package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.safeCallTyped
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toFirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.dataSources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthException
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.cancellation.CancellationException

class AuthenticationRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authManager: AuthManager
) : AuthenticationRemoteDataSource {

    override suspend fun authenticationSignOut(): Result<Boolean, LogOutErrorHMM> {
        return try {
            authManager.signOut()
            Result.Success(true)
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: FirebaseAuthException) {
            Result.Failure(LogOutErrorHMM.FirebaseException(e.message ?: ""))
        } catch (e: Exception) {
            Logger.e("Exception caught while logging out: ${e.message}")
            Result.Failure(LogOutErrorHMM.UnknownErrorHMM(e.message ?: ""))
        }
    }

    override suspend fun signInWithCredentials(
        credential: AuthCredential,
        providerType: ProviderType
    ): Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> {
        return try {
            when (val result = authManager.signIn(providerType, credential)) {
                is Result.Success -> Result.Success(result.data)
                is Result.Failure -> Result.Failure(SocialMediaErrorHMM.InvalidCredential(""))
                else -> Result.Failure(SocialMediaErrorHMM.InvalidCredential(""))
            }
        } catch (e: Exception) {
            Logger.e("Exception caught while signing in with credentials: ${e.message}")
            Result.Failure(SocialMediaErrorHMM.InvalidCredential(""))
        }
    }

    override suspend fun linkWithCredential(credential: AuthCredential): Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> {
        return try {
            val result = auth.currentUser?.linkWithCredential(credential)

            if (result?.user != null) {
                Result.Success(result.user!!.toFirebaseUserInfoDomainModel())
            } else {
                Result.Failure(SocialMediaErrorHMM.LinkSocialMediaErrorHMM(""))
            }
        } catch (e: Exception) {
            Logger.e("Exception caught while linking credentials: ${e.message}")
            Result.Failure(SocialMediaErrorHMM.LinkSocialMediaErrorHMM(e.message ?: ""))
        }
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, CreateUserWithEmailAndPasswordErrorHMM> =
        safeCallTyped<String, CreateUserWithEmailAndPasswordErrorHMM> {
            auth.createUserWithEmailAndPassword(email, password)
            Result.Success(auth.currentUser?.uid ?: "")
        }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordErrorHMM> =
        safeCallTyped<String, SignInWithEmailAndPasswordErrorHMM> {
            val result = auth.signInWithEmailAndPassword(email, password)
            val uid = result.user?.uid ?: throw Error("User not found")
            Result.Success(uid)
        }

    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordErrorHMM> =
        safeCallTyped<Boolean, ResetPasswordErrorHMM> {
            auth.sendPasswordResetEmail(email)
            Result.Success(true)
        }

    override suspend fun getUserUid(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun isFirstSignIn(uid: String): Boolean {
        if (uid.isEmpty()) {
            return true
        }

        val userDocument = firestore.collection("users").document(uid).get()
        // Return true if user does NOT exist
        return !userDocument.exists
    }

    override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel? {
        return auth.currentUser?.toFirebaseUserInfoDomainModel()
    }
}
