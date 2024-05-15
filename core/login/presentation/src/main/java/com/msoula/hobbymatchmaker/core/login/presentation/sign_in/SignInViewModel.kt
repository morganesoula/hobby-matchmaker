package com.msoula.hobbymatchmaker.core.login.presentation.sign_in

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignInUseCase
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider
import com.msoula.hobbymatchmaker.core.di.domain.useCase.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.di.navigation.AppScreenRoute
import com.msoula.hobbymatchmaker.core.di.navigation.Navigator
import com.msoula.hobbymatchmaker.core.login.presentation.R
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.clearAll
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.updateStateHandle
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEventModel
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.models.SignInFormStateModel
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.models.SignInResultModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authFormValidationUseCases: AuthFormValidationUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val signInUseCase: SignInUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val navigator: Navigator,
    private val resourceProvider: StringResourcesProvider
) : ViewModel() {

    private val savedStateHandleKey: String = "loginState"
    val formDataFlow = savedStateHandle.getStateFlow(savedStateHandleKey, SignInFormStateModel())
    val circularProgressLoading = MutableStateFlow(false)

    val openResetDialog = MutableStateFlow(false)
    val resettingEmailSent = MutableStateFlow(false)

    fun onEvent(event: AuthenticationUIEventModel) {
        when (event) {
            is AuthenticationUIEventModel.OnEmailChanged -> {
                savedStateHandle.updateStateHandle<SignInFormStateModel>(savedStateHandleKey) {
                    it.copy(
                        email = event.email.trimEnd()
                    )
                }
                validateInput()
            }

            is AuthenticationUIEventModel.OnEmailResetChanged -> {
                savedStateHandle.updateStateHandle<SignInFormStateModel>(savedStateHandleKey) {
                    it.copy(
                        emailReset = event.emailReset.trimEnd(),
                    )
                }
                validateEmailReset(event.emailReset)
            }

            is AuthenticationUIEventModel.OnPasswordChanged -> {
                savedStateHandle.updateStateHandle<SignInFormStateModel>(savedStateHandleKey) {
                    it.copy(
                        password = event.password.trimEnd()
                    )
                }
                validateInput()
            }

            AuthenticationUIEventModel.OnForgotPasswordClicked -> {
                if (!openResetDialog.value) {
                    openResetDialog.value = true
                }
            }

            AuthenticationUIEventModel.HideForgotPasswordDialog -> {
                if (openResetDialog.value) {
                    openResetDialog.value = false
                }
            }

            AuthenticationUIEventModel.OnResetPasswordConfirmed -> launchResetPassword()

            AuthenticationUIEventModel.OnLogIn -> {
                circularProgressLoading.value = true
                logIn()
            }

            else -> Unit
        }
    }

    fun onGoogleSignInEvent(result: SignInResultModel) {
        if (result.data != null) {
            Log.d("HMM", "Data is not null so navigating to HomeScreen page")
            navigator.navigate(AppScreenRoute)
        } else {
            Log.e("HMM", "Data is null so what do we do now?")
        }
    }

    private fun validateInput() {
        val emailResult = authFormValidationUseCases.validateEmailUseCase(formDataFlow.value.email)
        val passwordResult =
            authFormValidationUseCases.validatePasswordUseCase.validateLoginPassword(formDataFlow.value.password)

        val error = listOf(emailResult, passwordResult).any { !it.successful }

        if (error) {
            savedStateHandle.updateStateHandle<SignInFormStateModel>(savedStateHandleKey) {
                it.copy(
                    submit = false
                )
            }
            return
        } else {
            savedStateHandle.updateStateHandle<SignInFormStateModel>(savedStateHandleKey) {
                it.copy(
                    submit = true
                )
            }
        }
    }

    private fun validateEmailReset(emailReset: String): Boolean =
        authFormValidationUseCases.validateEmailUseCase(emailReset).successful

    private fun logIn() {
        viewModelScope.launch(ioDispatcher) {
            signInUseCase(
                formDataFlow.value.email,
                formDataFlow.value.password
            )
                .mapSuccess {
                    circularProgressLoading.value = false
                    savedStateHandle.clearAll<SignInFormStateModel>()

                    withContext(Dispatchers.Main) {
                        navigator.navigate(AppScreenRoute)
                    }
                }
                .mapError { error ->
                    circularProgressLoading.value = false
                    savedStateHandle.updateStateHandle<SignInFormStateModel>(
                        savedStateHandleKey
                    ) {
                        it.copy(
                            logInError =
                            resourceProvider.getString(
                                R.string.login_error,
                            ),
                        )
                    }
                    Log.e("HMM", "Invalid credentials")
                    error
                }
        }
    }

    private fun launchResetPassword() {
        viewModelScope.launch(ioDispatcher) {
            resetPassword()
        }
    }

    private suspend fun resetPassword() {
        resetPasswordUseCase(formDataFlow.value.emailReset)
            .mapSuccess {
                if (openResetDialog.value) {
                    openResetDialog.value = false
                    resettingEmailSent.value = true
                }
            }
            .mapError { error ->
                Log.e(
                    "HMM",
                    "Could not reset password with ${formDataFlow.value.emailReset} - $error"
                )
                resettingEmailSent.value = false
                error
            }
    }
}
