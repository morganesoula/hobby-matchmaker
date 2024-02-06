package com.msoula.auth.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.msoula.auth.domain.repository.AuthRepository
import com.msoula.auth.domain.repository.LogOutResponse
import com.msoula.auth.domain.repository.LoginResponse
import com.msoula.auth.domain.repository.ResetEmailResponse
import com.msoula.auth.domain.repository.Response
import com.msoula.auth.domain.repository.SignUpResponse
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override fun getAuthState(): Boolean {
        val authStateListener = AuthStateListener {
            if (auth.currentUser == null) {
                Log.d("HMM", "Inside getAuthState with user null")
            } else {
                Log.d("HMM", "Inside getAuthState but not completed it")
            }
        }

        auth.addAuthStateListener(authStateListener)

        return auth.currentUser == null
    }

    override suspend fun logOut(): LogOutResponse {
        return try {
            auth.signOut()
            Response.Success(true)
        } catch (exception: Exception) {
            Response.Failure(exception)
        }
    }

    override suspend fun signUp(email: String, password: String): SignUpResponse {
        return try {
            val result = auth.createUserWithEmailAndPassword(
                email, password
            ).await()
            Response.Success(result)
        } catch (exception: Exception) {
            Response.Failure(exception)
        }
    }

    override suspend fun loginWithEmailAndPassword(email: String, password: String): LoginResponse {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Response.Success(result)
        } catch (exception: Exception) {
            Response.Failure(exception)
        }
    }

    override suspend fun resetPassword(email: String): ResetEmailResponse {
        return try {
            auth.sendPasswordResetEmail(email)
            Response.Success(true)
        } catch (exception: Exception) {
            Response.Failure(exception)
        }
    }
}

