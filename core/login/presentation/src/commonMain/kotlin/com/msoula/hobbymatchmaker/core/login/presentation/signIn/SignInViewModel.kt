package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.UnifiedSignInUseCase
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.UIText
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.di.domain.useCases.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthUiEventModel
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.ResetPasswordEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.models.SignInFormStateModel
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveShouldShowGuestDialogUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetCurrentUserProfileUuidUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetShouldShowGuestDialogUseCase
import dev.gitlive.firebase.auth.AuthCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val authFormValidationUseCases: AuthFormValidationUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val setShouldShowGuestDialogUseCase: SetShouldShowGuestDialogUseCase,
    val observeShouldShowGuestDialog: ObserveShouldShowGuestDialogUseCase,
    val setCurrentUserProfileUuidUseCase: SetCurrentUserProfileUuidUseCase,
    private val unifiedSignInUseCase: UnifiedSignInUseCase,
    private val socialClients: Map<ProviderType, SocialUIClient>,
    private val defaultErrorMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    private val scope = externalScope ?: viewModelScope
    private val _oneTimeEventChannel = Channel<AuthUiEventModel>()
    val oneTimeEventChannelFlow = _oneTimeEventChannel.receiveAsFlow()
    private val _formDataFlow = MutableStateFlow(SignInFormStateModel())
    val formDataFlow = _formDataFlow.asStateFlow()
    val openResetDialog = MutableStateFlow(false)

    @VisibleForTesting
    internal var isSignIn = false

    private val _resetPasswordState: MutableStateFlow<ResetPasswordEvent> =
        MutableStateFlow(ResetPasswordEvent.Idle)
    val resetPasswordState: StateFlow<ResetPasswordEvent> = _resetPasswordState.asStateFlow()

    private val _signInState: MutableStateFlow<SignInEvent> =
        MutableStateFlow(SignInEvent.Idle)
    val signInState: StateFlow<SignInEvent> = _signInState.asStateFlow()

    private val _isGuestLoading = MutableStateFlow(false)
    val isGuestLoading: StateFlow<Boolean> = _isGuestLoading.asStateFlow()

    val shouldShowGuestDialog = observeShouldShowGuestDialog().stateIn(
        scope,
        SharingStarted.Eagerly, true
    )

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

            is AuthenticationUIEvent.OnContinueAsGuestConfirmed ->
                scope.launch {
                    when (setCurrentUserProfileUuidUseCase()) {
                        is AppResult.Success ->
                            setShouldShowGuestDialogUseCase(shouldShow = !event.dontAskAgain)

                        is AppResult.Failure -> {}
                    }
                }

            AuthenticationUIEvent.OnGoogleButtonClicked ->
                scope.launch {
                    launchSocialSignIn(ProviderType.GOOGLE)
                }

            AuthenticationUIEvent.OnAppleButtonClicked ->
                scope.launch {
                    launchSocialSignIn(ProviderType.APPLE)
                }

            is AuthenticationUIEvent.OnFacebookButtonClicked ->
                scope.launch {
                    launchSocialSignIn(ProviderType.FACEBOOK, event.credential)
                }

            AuthenticationUIEvent.OnResetPasswordConfirmed -> {
                scope.launch { resetPassword() }
            }

            AuthenticationUIEvent.OnSignIn ->
                scope.launch {
                    signInUnified(
                        UnifiedSignInUseCase.Params.EmailPassword(
                            email = formDataFlow.value.email,
                            password = formDataFlow.value.password
                        )
                    )
                }

            AuthenticationUIEvent.OnScreenChanged -> resetForm()

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

    private suspend fun signInUnified(params: UnifiedSignInUseCase.Params) {
        _signInState.update { SignInEvent.Loading }

        unifiedSignInUseCase(params)
            .onFailure {
                resetSignInState()
                val uiError = defaultErrorMessageMapper.toUIText(it)
                sendOnce(AuthUiEventModel.ShowError(uiError))
            }
            .onSuccess { result ->
                setCurrentUserProfileUuidUseCase(result.uid)
                resetSignInState()
                sendOnce(AuthUiEventModel.OnSignInSuccess)
            }
    }

    private suspend fun launchSocialSignIn(
        providerType: ProviderType,
        fetchedCredential: AuthCredential? = null
    ) {
        if (isSignIn) return
        isSignIn = true

        val client = socialClients[providerType]
        val credential = fetchedCredential ?: client?.getCredential()

        if (credential != null) {
            signInUnified(
                UnifiedSignInUseCase.Params.SocialMedia(
                    credential, providerType
                )
            )
        } else {
            sendOnce(
                AuthUiEventModel.ShowError(
                    UIText.Plain("Unable to get credentials")
                )
            )
            isSignIn = false
        }
    }

    private suspend fun resetPassword() {
        if (!formDataFlow.value.submitEmailReset) return
        _resetPasswordState.update { ResetPasswordEvent.Loading }

        val email = formDataFlow.value.emailReset

        resetPasswordUseCase(Parameters.StringParam(email))
            .onFailure {
                resetResetState()
                val uiError = defaultErrorMessageMapper.toUIText(it)
                sendOnce(AuthUiEventModel.ShowError(uiError))
            }
            .onSuccess {
                resetResetState()
                _formDataFlow.update { it.copy(emailReset = "") }
                sendOnce(AuthUiEventModel.OnResetPasswordSuccess)
            }
    }

    fun resetSignInState() = _signInState.update { SignInEvent.Idle }
    fun resetResetState() = _resetPasswordState.update { ResetPasswordEvent.Idle }

    fun resetForm() {
        _formDataFlow.update { SignInFormStateModel() }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun sendOnce(event: AuthUiEventModel) {
        if (!_oneTimeEventChannel.isClosedForSend) {
            _oneTimeEventChannel.send(event)
        }
    }
}
