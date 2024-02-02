package com.msoula.auth.data

import android.os.Parcel
import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.msoula.auth.domain.repository.AuthRepository
import com.msoula.auth.domain.repository.LogOutResponse
import com.msoula.auth.domain.repository.LoginResponse
import com.msoula.auth.domain.repository.ResetEmailResponse
import com.msoula.auth.domain.repository.Response
import com.msoula.auth.domain.repository.SignUpResponse
import com.msoula.di.domain.use_case.ValidateEmail
import kotlinx.coroutines.CoroutineScope

class FakeAuthRepositoryImpl : AuthRepository {

    override fun getAuthState(viewModelScope: CoroutineScope): Boolean = true

    override suspend fun logOut(): LogOutResponse {
        return Response.Success(true)
    }

    override suspend fun signUp(email: String, password: String): SignUpResponse {
        // Add random character just to test Response.Failure
        return if (password.contains("?")) {
            Response.Success(FakeAuthResult())
        } else {
            Response.Failure(FirebaseAuthUserCollisionException("test", "test"))
        }
    }

    override suspend fun loginWithEmailAndPassword(email: String, password: String): LoginResponse {
        return if (ValidateEmail().invoke(email).successful && password.length >= 8) {
            Response.Success(FakeAuthResult())
        } else {
            Response.Failure(FirebaseAuthInvalidCredentialsException("test", "test"))
        }
    }

    override suspend fun resetPassword(email: String): ResetEmailResponse {
        return if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex())) {
            Response.Success(true)
        } else {
            Response.Failure(Exception("invalid email format"))
        }
    }
}

class FakeAuthResult : AuthResult {
    override fun getUser(): FirebaseUser? {
        TODO("Not yet implemented")
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    override fun getAdditionalUserInfo(): AdditionalUserInfo? {
        TODO("Not yet implemented")
    }

    override fun getCredential(): AuthCredential? {
        TODO("Not yet implemented")
    }
}