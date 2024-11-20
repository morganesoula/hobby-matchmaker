package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInError
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SocialMediaSignInUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider
import com.msoula.hobbymatchmaker.core.di.domain.useCase.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.R
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.clearAll
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.updateStateHandle
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.ResetPasswordEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.models.SignInFormStateModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("NewApi")
class SignInViewModel(
    private val authFormValidationUseCases: AuthFormValidationUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val signInUseCase: SignInUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val resourceProvider: StringResourcesProvider,
    private val socialMediaSignInUseCase: SocialMediaSignInUseCase
) : ViewModel() {

    private val savedStateHandleKey: String = "loginState"
    val formDataFlow = savedStateHandle.getStateFlow(savedStateHandleKey, SignInFormStateModel())
    val circularProgressLoading = MutableStateFlow(false)

    private val _oneTimeEventChannel = Channel<AuthenticationEvent>()
    val oneTimeEventChannelFlow = _oneTimeEventChannel.receiveAsFlow()

    val openResetDialog = MutableStateFlow(false)

    private val _resetPasswordState: MutableStateFlow<ResetPasswordEvent> =
        MutableStateFlow(ResetPasswordEvent.Idle)
    val resetPasswordState: StateFlow<ResetPasswordEvent> = _resetPasswordState.asStateFlow()

    private val _signInState: MutableStateFlow<SignInEvent> =
        MutableStateFlow(SignInEvent.Idle)
    val signInState: StateFlow<SignInEvent> = _signInState.asStateFlow()

    fun onEvent(event: AuthenticationUIEvent) {
        when (event) {
            is AuthenticationUIEvent.OnEmailChanged -> {
                updateFormState { it.copy(email = event.email.trimEnd()) }
                validateInput()
            }

            is AuthenticationUIEvent.OnEmailResetChanged -> {
                updateFormState {
                    it.copy(
                        emailReset = event.emailReset.trimEnd(),
                        submitEmailReset = validateEmailReset(event.emailReset)
                    )
                }
            }

            is AuthenticationUIEvent.OnPasswordChanged -> {
                updateFormState { it.copy(password = event.password.trimEnd()) }
                validateInput()
            }

            AuthenticationUIEvent.OnForgotPasswordClicked -> {
                openResetDialog.update { true }
            }

            AuthenticationUIEvent.HideForgotPasswordDialog -> {
                openResetDialog.update { false }
            }

            AuthenticationUIEvent.OnResetPasswordConfirmed -> viewModelScope.launch { resetPassword() }

            AuthenticationUIEvent.OnSignIn -> {
                viewModelScope.launch { signIn() }
            }

            else -> Unit
        }
    }

    fun connectWithSocialMedia(
        facebookAccessToken: AccessToken? = null,
        context: Context
    ) {
        (
            viewModelScope.launch {
                socialMediaSignInUseCase(
                    Parameters.GetCredentialResponseParam(
                        facebookAccessToken, context
                    )
                ).collectLatest { socialMediaResult ->
                    _signInState.value = when (socialMediaResult) {
                        is Result.Loading -> {
                            circularProgressLoading.value = true
                            SignInEvent.Loading
                        }

                        is Result.Success -> SignInEvent.Success
                        is Result.Failure -> {
                            circularProgressLoading.value = false
                            SignInEvent.Error(socialMediaResult.error.message)
                        }
                    }
                }
            })
    }

    private fun validateInput() {
        val emailResult = authFormValidationUseCases.validateEmailUseCase(formDataFlow.value.email)
        val passwordResult =
            authFormValidationUseCases.validatePasswordUseCase.validateLoginPassword(formDataFlow.value.password)
        val hasError = listOf(emailResult, passwordResult).any { !it.successful }

        updateFormState { it.copy(submit = !hasError) }
    }

    private fun validateEmailReset(emailReset: String): Boolean =
        authFormValidationUseCases.validateEmailUseCase(emailReset).successful

    private suspend fun signIn() {
        signInUseCase(
            Parameters.DoubleStringParam(
                formDataFlow.value.email,
                formDataFlow.value.password
            )
        ).collectLatest { result ->
            _signInState.update {
                when (result) {
                    is Result.Loading -> {
                        circularProgressLoading.value = true
                        SignInEvent.Loading
                    }

                    is Result.Failure -> {
                        circularProgressLoading.value = false
                        val errorMessage = handleError(result.error)
                        SignInEvent.Error(errorMessage)
                    }

                    is Result.Success -> {
                        clearFormState()
                        SignInEvent.Success
                    }
                }
            }
        }
    }

    private suspend fun resetPassword() {
        resetPasswordUseCase(Parameters.StringParam(formDataFlow.value.emailReset)).collectLatest { result ->
            _resetPasswordState.update {
                when (result) {
                    is Result.Loading -> ResetPasswordEvent.Loading
                    is Result.Success -> {
                        updateFormState { it.copy(emailReset = "") }
                        ResetPasswordEvent.Success
                    }

                    is Result.Failure -> ResetPasswordEvent.Error(result.error.message)
                }
            }
        }
    }

    private fun handleError(error: AppError): String {
        return when (error) {
            is SignInError.WrongPassword -> resourceProvider.getString(R.string.login_error)
            is SignInError.UserNotFound -> resourceProvider.getString(
                R.string.user_not_found_error
            )

            is SignInError.UserDisabled -> resourceProvider.getString(
                R.string.user_disabled_error
            )

            is SignInError.TooManyRequests -> resourceProvider.getString(
                R.string.too_many_requests_error
            )

            else -> error.message
        }
    }

    private fun updateFormState(update: (SignInFormStateModel) -> SignInFormStateModel) =
        savedStateHandle.updateStateHandle(savedStateHandleKey, update)

    private fun clearFormState() = savedStateHandle.clearAll<SignInFormStateModel>()
}
