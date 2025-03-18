package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInError
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInWithCredentialUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.StateSaver
import com.msoula.hobbymatchmaker.core.di.domain.useCases.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.Res
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AppleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.login_error
import com.msoula.hobbymatchmaker.core.login.presentation.malformed_sign_in_error
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.ResetPasswordEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.models.SignInFormStateModel
import com.msoula.hobbymatchmaker.core.login.presentation.too_many_requests_error
import com.msoula.hobbymatchmaker.core.login.presentation.user_disabled_error
import com.msoula.hobbymatchmaker.core.login.presentation.user_not_found_error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString

class SignInViewModel(
    private val authFormValidationUseCases: AuthFormValidationUseCase,
    private val stateSaver: StateSaver,
    private val signInUseCase: SignInUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val signInWithCredentialUseCase: SignInWithCredentialUseCase,
    private val googleUIClient: GoogleUIClient,
    private val appleUIClient: AppleUIClient?,
    private val facebookUIClient: FacebookUIClient?
) : ScreenModel {

    private val savedStateHandleKey: String = "loginState"

    val formDataFlow = stateSaver.getStateFlow(savedStateHandleKey, SignInFormStateModel())
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

            AuthenticationUIEvent.OnGoogleButtonClicked -> {
                Logger.d("GoogleUIClient in SignInVM is: $googleUIClient")
                signInWithSocial(ProviderType.GOOGLE, googleUIClient = googleUIClient)
            }

            AuthenticationUIEvent.OnAppleButtonClicked -> {
                signInWithSocial(ProviderType.APPLE, appleUIClient = appleUIClient)
            }

            AuthenticationUIEvent.OnFacebookButtonClicked -> {
                signInWithSocial(ProviderType.FACEBOOK, facebookUIClient = facebookUIClient)
            }

            AuthenticationUIEvent.OnResetPasswordConfirmed -> screenModelScope.launch { resetPassword() }

            AuthenticationUIEvent.OnSignIn -> {
                screenModelScope.launch { signIn() }
            }

            else -> Unit
        }
    }

    private fun signInWithSocial(
        providerType: ProviderType,
        googleUIClient: GoogleUIClient? = null,
        appleUIClient: AppleUIClient? = null,
        facebookUIClient: FacebookUIClient? = null
    ) {
        (
            screenModelScope.launch(Dispatchers.IO) {
                val credentials = when (providerType) {
                    ProviderType.GOOGLE -> googleUIClient?.getGoogleCredentials()
                    ProviderType.APPLE -> appleUIClient?.getAppleCredentials()
                    ProviderType.FACEBOOK -> facebookUIClient?.getFacebookCredentials()?.first
                }

                if (credentials != null) {
                    withContext(Dispatchers.Main) {
                        val socialMediaResult =
                            signInWithCredentialUseCase(
                                credentials,
                                providerType
                            )
                        _signInState.value = when (socialMediaResult) {
                            is Result.Loading -> {
                                circularProgressLoading.value = true
                                SignInEvent.Loading
                            }

                            is Result.Success -> {
                                circularProgressLoading.value = false
                                SignInEvent.Success
                            }

                            is Result.Failure -> {
                                circularProgressLoading.value = false
                                SignInEvent.Error(socialMediaResult.error.message)
                            }

                            else -> SignInEvent.Loading
                        }
                    }
                } else {
                    circularProgressLoading.value = false
                    SignInEvent.Error("Error while loading Google credentials")
                }
            }
            )
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
                        circularProgressLoading.value = false
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

    private suspend fun handleError(error: AppError): String {
        return when (error) {
            is SignInError.WrongPassword -> getString(Res.string.login_error)
            is SignInError.UserNotFound -> getString(
                Res.string.user_not_found_error
            )

            is SignInError.UserDisabled -> getString(
                Res.string.user_disabled_error
            )

            is SignInError.TooManyRequests -> getString(
                Res.string.too_many_requests_error
            )

            else -> if (error.message.contains("incorrect")) getString(Res.string.malformed_sign_in_error) else error.message
        }
    }

    private fun updateFormState(update: (SignInFormStateModel) -> SignInFormStateModel) {
        formDataFlow.value = update(formDataFlow.value)
    }

    private fun clearFormState() = stateSaver.removeState("loginState")
}
