package com.msoula.auth.presentation

import android.util.Log
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authFormValidationUseCases: AuthFormValidationUseCase,
    private val authRepository: AuthRepository,
    private val resourceProvider: StringResourcesProvider,
    private val ioDispatcher: CoroutineDispatcher,
    private val navigator: Navigator
) : ViewModel() {

    private val formDataFlow = MutableStateFlow(LoginFormState())

    val loginFormState = formDataFlow.map { formData ->
        LoginFormState(
            email = formData.email,
            password = formData.password,
            emailReset = formData.emailReset,
            submit = validateInput(formData),
            submitEmailReset = validateEmailReset(formData.emailReset),
            logInError = formData.logInError
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, LoginFormState())

    val circularProgressLoading = MutableStateFlow(false)
    val openResetDialog = MutableStateFlow(false)
    val resettingEmailSent = MutableStateFlow(false)

    fun onEvent(event: AuthUIEvent) {
        when (event) {
            is AuthUIEvent.OnEmailChanged -> formDataFlow.update { it.copy(email = event.email.trimEnd()) }
            is AuthUIEvent.OnEmailResetChanged -> {
                formDataFlow.update { it.copy(emailReset = event.emailReset.trimEnd()) }
                validateEmailReset(event.emailReset)
            }

            is AuthUIEvent.OnPasswordChanged -> formDataFlow.update { it.copy(password = event.password.trimEnd()) }
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

            AuthUIEvent.OnResetPasswordConfirmed -> launchResetPassword()
            AuthUIEvent.OnLogIn -> {
                circularProgressLoading.value = true
                logIn()
            }

            else -> Unit
        }
    }

    private fun validateInput(formData: LoginFormState): Boolean {
        val emailResult = authFormValidationUseCases.validateEmail(formData.email)
        val passwordResult =
            authFormValidationUseCases.validatePassword.validateLoginPassword(formData.password)

        return listOf(emailResult, passwordResult).all { it.successful }
    }

    private fun validateEmailReset(emailReset: String): Boolean =
        authFormValidationUseCases.validateEmail(emailReset).successful

    private fun logIn() {
        viewModelScope.launch(ioDispatcher) {
            try {
                when (val result = authRepository.loginWithEmailAndPassword(
                    formDataFlow.value.email,
                    formDataFlow.value.password
                )) {
                    is Response.Success -> {
                        circularProgressLoading.value = false
                        println("HMM-TEST Successfully logged in")
                        withContext(Dispatchers.Main) {
                            navigator.navigate(HomeScreenRoute)
                        }
                    }

                    is Response.Failure -> {
                        circularProgressLoading.value = false
                        when (result.exception) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                formDataFlow.update {
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
            when (authRepository.resetPassword(loginFormState.value.emailReset)) {
                is Response.Success -> {
                    if (openResetDialog.value) {
                        openResetDialog.value = false
                    }
                    true
                }

                else -> {
                    Log.e("HMM", "Could not reset password with ${loginFormState.value.emailReset}")
                    false
                }
            }

        } catch (exception: Exception) {
            Log.e("HMM", "Failed to send e-mail to reset password")
            false
        }
    }
}

