package com.msoula.auth.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.msoula.auth.R
import com.msoula.auth.data.AuthUIEvent
import com.msoula.auth.data.LoginFormState
import com.msoula.auth.data.SignInResult
import com.msoula.auth.domain.repository.AuthRepository
import com.msoula.di.domain.StringResourcesProvider
import com.msoula.di.domain.useCase.AuthFormValidationUseCase
import com.msoula.di.navigation.HomeScreenRoute
import com.msoula.di.navigation.Navigator
import com.msoula.network.ResponseHMM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val authFormValidationUseCases: AuthFormValidationUseCase,
    private val authRepository: AuthRepository,
    private val resourceProvider: StringResourcesProvider,
    private val ioDispatcher: CoroutineDispatcher,
    private val navigator: Navigator,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val savedStateHandleKey: String = "loginState"
    val formDataFlow = savedStateHandle.getStateFlow(savedStateHandleKey, LoginFormState())
    val circularProgressLoading = MutableStateFlow(false)

    val openResetDialog = MutableStateFlow(false)
    val resettingEmailSent = MutableStateFlow(false)

    fun onEvent(event: AuthUIEvent) {
        when (event) {
            is AuthUIEvent.OnEmailChanged -> {
                savedStateHandle.updateStateHandle<LoginFormState>(savedStateHandleKey) { it.copy(email = event.email.trimEnd()) }
                validateInput()
            }

            is AuthUIEvent.OnEmailResetChanged -> {
                savedStateHandle.updateStateHandle<LoginFormState>(savedStateHandleKey) {
                    it.copy(
                        emailReset = event.emailReset.trimEnd()
                    )
                }
                validateEmailReset(event.emailReset)
            }

            is AuthUIEvent.OnPasswordChanged -> {
                savedStateHandle.updateStateHandle<LoginFormState>(savedStateHandleKey) { it.copy(password = event.password.trimEnd()) }
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

            AuthUIEvent.OnResetPasswordConfirmed -> launchResetPassword()

            AuthUIEvent.OnLogIn -> {
                circularProgressLoading.value = true
                logIn()
            }

            else -> Unit
        }
    }

    fun onGoogleSignInEvent(result: SignInResult) {
        if (result.data != null) {
            Log.d("HMM", "Data is not null so navigating to HomeScreen page")
            navigator.navigate(HomeScreenRoute)
        } else {
            Log.e("HMM", "Data is null so what do we do now?")
        }
    }

    private fun validateInput() {
        val emailResult = authFormValidationUseCases.validateEmail(formDataFlow.value.email)
        val passwordResult =
            authFormValidationUseCases.validatePassword.validateLoginPassword(formDataFlow.value.password)

        val error = listOf(emailResult, passwordResult).any { !it.successful }

        if (error) {
            savedStateHandle.updateStateHandle<LoginFormState>(savedStateHandleKey) { it.copy(submit = false) }
            return
        } else {
            savedStateHandle.updateStateHandle<LoginFormState>(savedStateHandleKey) { it.copy(submit = true) }
        }
    }

    private fun validateEmailReset(emailReset: String): Boolean =
        authFormValidationUseCases.validateEmail(emailReset).successful

    private fun logIn() {
        viewModelScope.launch(ioDispatcher) {
            try {
                when (
                    val result =
                        authRepository.loginWithEmailAndPassword(
                            formDataFlow.value.email,
                            formDataFlow.value.password,
                        )
                ) {
                    is ResponseHMM.Success -> {
                        circularProgressLoading.value = false
                        savedStateHandle.clearAll<LoginFormState>()

                        withContext(Dispatchers.Main) {
                            navigator.navigate(HomeScreenRoute)
                        }
                    }

                    is ResponseHMM.Failure -> {
                        circularProgressLoading.value = false
                        when (result.throwable) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                savedStateHandle.updateStateHandle<LoginFormState>(savedStateHandleKey) {
                                    it.copy(
                                        logInError = resourceProvider.getString(
                                            R.string.login_error
                                        )
                                    )
                                }
                                Log.e("HMM", "Invalid credentials")
                            }

                            else -> {
                                Log.e("HMM", "Could not log in")
                            }
                        }
                    }

                    else -> Unit
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
            when (authRepository.resetPassword(formDataFlow.value.emailReset)) {
                is ResponseHMM.Success -> {
                    if (openResetDialog.value) {
                        openResetDialog.value = false
                    }
                    true
                }

                else -> {
                    Log.e("HMM", "Could not reset password with ${formDataFlow.value.emailReset}")
                    false
                }
            }
        } catch (exception: Exception) {
            Log.e("HMM", "Failed to send e-mail to reset password")
            false
        }
    }
}
