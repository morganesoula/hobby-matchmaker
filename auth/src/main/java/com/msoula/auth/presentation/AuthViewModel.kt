package com.msoula.auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.msoula.auth.data.AuthFormState
import com.msoula.auth.data.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(): ViewModel() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _userState = MutableStateFlow(UserState(auth.currentUser))
    val userState: StateFlow<UserState?> = _userState.asStateFlow()

    private val _authFormState = MutableStateFlow(AuthFormState())
    val authFormState: StateFlow<AuthFormState> = _authFormState.asStateFlow()

    fun onFormEvent(event: AuthFormEvent) {
        when (event) {
            is AuthFormEvent.OnEmailChanged -> _authFormState.update { it.copy(email = event.email) }
            is AuthFormEvent.OnPasswordChanged -> _authFormState.update { it.copy(password = event.password) }
            is AuthFormEvent.OnSubmitAuthForm -> signIn()
        }
    }

    private fun signIn() {
        auth.signInWithEmailAndPassword(
            userState.value?.email ?: "",
            userState.value?.password ?: ""
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("HMM", "Successful signing")
                _userState.update {
                    it.copy(connected = true)
                }
            } else {
                Log.e("HMM", "Error in Firebase logging")
            }
        }
    }
}