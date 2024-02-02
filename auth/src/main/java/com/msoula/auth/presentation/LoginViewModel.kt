package com.msoula.auth.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.msoula.auth.R
import com.msoula.auth.data.AuthUIEvent
import com.msoula.auth.data.LoginFormState
import com.msoula.auth.domain.repository.AuthRepository
import com.msoula.auth.domain.repository.Response
import com.msoula.di.domain.StringResourcesProvider
import com.msoula.di.domain.use_case.AuthFormValidationUseCase
import com.msoula.di.navigation.HomeScreenRoute
import com.msoula.di.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LoginViewModel @Inject constructor(
    @Named("authFormUseCases") private val authFormValidationUseCases: AuthFormValidationUseCase,
    @Named("authRepository") private val authRepository: AuthRepository,
    @Named("resourceProvider") private val resourceProvider: StringResourcesProvider,
    @Named("ioDispatcher") private val ioDispatcher: CoroutineDispatcher,
    private val navigator: Navigator
) : ViewModel() {

    private val _logInState = MutableStateFlow(LoginFormState())
    val logInState = _logInState.asStateFlow()

    val circularProgressLoading = mutableStateOf(false)
    val openResetDialog = mutableStateOf(false)
    val resettingEmailSent = mutableStateOf(false)

    fun onEvent(event: AuthUIEvent) {
        when (event) {
            is AuthUIEvent.OnEmailChanged -> {
                _logInState.update { it.copy(email = event.email) }
                validateInput()
            }

            is AuthUIEvent.OnEmailResetChanged -> {
                _logInState.update { it.copy(emailReset = event.emailReset) }
                validateEmailReset(event.emailReset)
            }

            is AuthUIEvent.OnPasswordChanged -> {
                _logInState.update { it.copy(password = event.password) }
                validateInput()
            }

            AuthUIEvent.OnForgotPasswordClicked -> {
                if (!openResetDialog.value) {
                    openResetDialog.value = true
                }
            }

            AuthUIEvent.HideForgotPasswordDialog -> {
                if (openResetDialog.value) {
                    openResetDialog.value = false
                }
            }

            AuthUIEvent.OnResetPasswordConfirmed -> {
                launchResetPassword()
            }

            AuthUIEvent.OnLogIn -> {
                circularProgressLoading.value = true
                logIn()
            }

            else -> Unit
        }
    }

    private fun validateInput() {
        val emailResult = authFormValidationUseCases.validateEmail(logInState.value.email)
        val passwordResult =
            authFormValidationUseCases.validatePassword.validateLoginPassword(logInState.value.password)

        val error = listOf(emailResult, passwordResult).any { !it.successful }

        if (error) {
            if (logInState.value.submit) _logInState.update { it.copy(submit = false) }
            return
        } else {
            _logInState.update { it.copy(submit = true) }
        }
    }

    private fun validateEmailReset(emailReset: String) {
        val emailResult = authFormValidationUseCases.validateEmail(emailReset)

        val error = !emailResult.successful

        if (error) {
            if (logInState.value.submitEmailReset) _logInState.update { it.copy(submitEmailReset = false) }
            return
        } else {
            _logInState.update { it.copy(submitEmailReset = true) }
        }
    }

    private fun logIn() {
        viewModelScope.launch(ioDispatcher) {
            try {
                when (val result = authRepository.loginWithEmailAndPassword(
                    logInState.value.email,
                    logInState.value.password
                )) {
                    is Response.Success -> {
                        circularProgressLoading.value = false
                        println("HMM-TEST Sucessfully logged in")
                        withContext(Dispatchers.Main) {
                            navigator.navigate(HomeScreenRoute)
                        }
                    }

                    is Response.Failure -> {
                        circularProgressLoading.value = false
                        when (result.exception) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                _logInState.update {
                                    it.copy(
                                        logInError = resourceProvider.getString(R.string.login_error)
                                    )
                                }
                                Log.e("HMM", "Invalid credentials")
                            }

                            else -> {
                                Log.e("HMM", "Could not log in")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("HMM-TEST Failed to launch logIn(): $e")
                Log.e("HMM", "Failed to launch logIn() method with exception: $e")
            }
        }
    }

    private fun launchResetPassword() {
        viewModelScope.launch(ioDispatcher) {
            resettingEmailSent.value = resetPassword()
        }
    }

    private suspend fun resetPassword(): Boolean {
        return try {
            when (authRepository.resetPassword(logInState.value.emailReset)) {
                is Response.Success -> {
                    if (openResetDialog.value) {
                        openResetDialog.value = false
                    }
                    true
                }

                else -> {
                    Log.e("HMM", "Could not reset password with ${logInState.value.emailReset}")
                    false
                }
            }

        } catch (exception: Exception) {
            Log.e("HMM", "Failed to send e-mail to reset password")
            false
        }
    }
}

