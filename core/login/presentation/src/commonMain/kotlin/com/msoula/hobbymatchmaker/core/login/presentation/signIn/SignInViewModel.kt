package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.UnifiedSignInUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.di.domain.useCases.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.ResetPasswordEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.models.SignInFormStateModel
import dev.gitlive.firebase.auth.AuthCredential
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val authFormValidationUseCases: AuthFormValidationUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val unifiedSignInUseCase: UnifiedSignInUseCase,
    private val socialClients: Map<ProviderType, SocialUIClient>,
    private val ioDispatcher: CoroutineDispatcher,
    private val errorMessageProvider: ErrorMessageProvider,
    private val externalScope: CoroutineScope? = null
) : ViewModel() {
    private val scope = externalScope ?: viewModelScope

    private val _formDataFlow = MutableStateFlow(SignInFormStateModel())
    val formDataFlow = _formDataFlow.asStateFlow()

    val circularProgressLoading = MutableStateFlow(false)
    val openResetDialog = MutableStateFlow(false)

    @VisibleForTesting
    internal var isSignIn = false

    private val _resetPasswordState: MutableStateFlow<ResetPasswordEvent> =
        MutableStateFlow(ResetPasswordEvent.Idle)
    val resetPasswordState: StateFlow<ResetPasswordEvent> = _resetPasswordState.asStateFlow()

    private val _signInState: MutableStateFlow<SignInEvent> =
        MutableStateFlow(SignInEvent.Idle)
    val signInState: StateFlow<SignInEvent> = _signInState.asStateFlow()

    fun onEvent(event: AuthenticationUIEvent) {
        when (event) {
            is AuthenticationUIEvent.OnEmailChanged -> {
                _formDataFlow.update { it.copy(email = event.email.trimEnd()) }
                validateInput()
            }

            is AuthenticationUIEvent.OnEmailResetChanged -> {
                _formDataFlow.update {
                    it.copy(
                        emailReset = event.emailReset.trimEnd(),
                        submitEmailReset = validateEmailReset(event.emailReset)
                    )
                }
            }

            is AuthenticationUIEvent.OnPasswordChanged -> {
                _formDataFlow.update { it.copy(password = event.password.trimEnd()) }
                validateInput()
            }

            AuthenticationUIEvent.OnForgotPasswordClicked ->
                openResetDialog.update { true }

            AuthenticationUIEvent.HideForgotPasswordDialog ->
                openResetDialog.update { false }

            AuthenticationUIEvent.OnGoogleButtonClicked ->
                launchSocialSignIn(ProviderType.GOOGLE)

            AuthenticationUIEvent.OnAppleButtonClicked ->
                launchSocialSignIn(ProviderType.APPLE)

            is AuthenticationUIEvent.OnFacebookButtonClicked ->
                launchSocialSignIn(ProviderType.FACEBOOK, event.credential)

            AuthenticationUIEvent.OnResetPasswordConfirmed ->
                scope.launch { resetPassword() }

            AuthenticationUIEvent.OnSignIn ->
                signInUnified(
                    UnifiedSignInUseCase.Params.EmailPassword(
                        email = formDataFlow.value.email,
                        password = formDataFlow.value.password
                    )
                )

            else -> Unit
        }
    }

    private fun validateInput() {
        val emailResult = authFormValidationUseCases.validateEmailUseCase(formDataFlow.value.email)
        val passwordResult =
            authFormValidationUseCases.validatePasswordUseCase.validateLoginPassword(formDataFlow.value.password)
        val hasError = listOf(emailResult, passwordResult).any { !it.successful }

        _formDataFlow.update { it.copy(submit = !hasError) }
    }

    private fun validateEmailReset(emailReset: String): Boolean =
        authFormValidationUseCases.validateEmailUseCase(emailReset).successful

    private fun signInUnified(params: UnifiedSignInUseCase.Params) {
        scope.launch(ioDispatcher) {
            unifiedSignInUseCase.signIn(params).collectLatest { result ->
                _signInState.value = when (result) {
                    is Result.Loading -> {
                        circularProgressLoading.value = true
                        SignInEvent.Loading
                    }

                    is Result.Success -> {
                        circularProgressLoading.value = false
                        isSignIn = false
                        SignInEvent.Success
                    }

                    is Result.Failure -> {
                        circularProgressLoading.value = false
                        isSignIn = false
                        val message = handleError(result.error)
                        SignInEvent.Error(message)
                    }
                }
            }
        }
    }

    private fun launchSocialSignIn(
        providerType: ProviderType,
        fetchedCredential: AuthCredential? = null
    ) {
        if (isSignIn) return
        isSignIn = true

        scope.launch(ioDispatcher) {
            val client = socialClients[providerType]
            val credential = fetchedCredential ?: client?.getCredential()

            if (credential != null) {
                signInUnified(
                    UnifiedSignInUseCase.Params.SocialMedia(
                        credential, providerType
                    )
                )
            } else {
                Logger.e("Could not load social credentials")
                _signInState.value = SignInEvent.Error("Unable to get credentials")
                isSignIn = false
            }
        }
    }

    private suspend fun resetPassword() {
        if (formDataFlow.value.submitEmailReset) {
            resetPasswordUseCase(Parameters.StringParam(formDataFlow.value.emailReset)).collect { result ->
                _resetPasswordState.update {
                    when (result) {
                        is Result.Loading -> ResetPasswordEvent.Loading
                        is Result.Success -> {
                            _formDataFlow.update { it.copy(emailReset = "") }
                            ResetPasswordEvent.Success
                        }

                        is Result.Failure -> {
                            val errorMessage = handleError(result.error)
                            ResetPasswordEvent.Error(errorMessage)
                        }
                    }
                }
            }
        }
    }

    private suspend fun handleError(error: AppError): String =
        errorMessageProvider.getMessage(error)

    fun resetSignInState() {
        _signInState.value = SignInEvent.Idle
    }
}
