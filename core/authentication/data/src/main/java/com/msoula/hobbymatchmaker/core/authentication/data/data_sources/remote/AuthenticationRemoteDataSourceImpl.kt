package com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote

import android.util.Log
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.CreateUserError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.SignInError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.mappers.toUserDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.models.UserDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthenticationRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val oneTapClient: SignInClient
) : AuthenticationRemoteDataSource {

    private val facebookManager = LoginManager.getInstance()

    override fun authenticationSignOut() {
        auth.signOut()
        Log.d("HMM", "Logged out from Email/Pwd")
    }

    override suspend fun oneTapClientSignOut() {
        oneTapClient.signOut()
        Log.d("HMM", "Logged out from Google")
    }

    override fun loginManagerSignOut() {
        facebookManager.logOut()
        Log.d("HMM", "Logged out from Facebook")
    }

    override fun getCurrentAuthenticationUser(): Flow<UserDomainModel?> = flow {
        emit(auth.toUserDomainModel())
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Boolean> {
        var result: Result<Boolean> = Result.Success(true)

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            result = if (it.isSuccessful) Result.Success(true) else
                Result.Failure(
                    CreateUserError(
                        message = it.exception?.message ?: "Could not create user"
                    )
                )
        }

        return result
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Boolean> {
        var result: Result<Boolean> = Result.Success(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                result = if (it.isSuccessful) {
                    Result.Success(true)
                } else
                    Result.Failure(
                        SignInError(
                            message = it.exception?.message ?: "Could not sign in"
                        )
                    )
            }

        return result
    }

    override suspend fun resetPassword(email: String): Result<Boolean> {
        var result: Result<Boolean> = Result.Success(true)

        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            result =
                if (it.isSuccessful) Result.Success(true) else Result.Failure(
                    ResetPasswordError(
                        message = it.exception?.message ?: "Could not reset password"
                    )
                )
        }

        return result
    }
}
