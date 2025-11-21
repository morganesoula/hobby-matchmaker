package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.UnifiedSignInUseCase
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.models.SignInFormStateModel
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveDontAskCheckboxValueUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetCurrentUserProfileUuidUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetDontAskGuestDialogUseCase
import dev.gitlive.firebase.auth.AuthCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class SignInViewModel(
    private val authFormValidationUseCases: LoginValidateFormUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val setDontAskGuestDialogUseCase: SetDontAskGuestDialogUseCase,
    val observeDontAskCheckboxValueUseCase: ObserveDontAskCheckboxValueUseCase,
    val setCurrentUserProfileUuidUseCase: SetCurrentUserProfileUuidUseCase,
    private val unifiedSignInUseCase: UnifiedSignInUseCase,
    private val socialClients: Map<ProviderType, SocialUIClient>,
    private val defaultErrorMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope
    private val eventHandler = EventHandler()
    val events = eventHandler.events
    private val _formDataFlow = MutableStateFlow(SignInFormStateModel())
    val formDataFlow = _formDataFlow.asStateFlow()
    private val _signInState: MutableStateFlow<UiState<Unit>> =
        MutableStateFlow(UiState.Success(Unit))
    val signInState: StateFlow<UiState<Unit>> = _signInState.asStateFlow()

    @VisibleForTesting
    internal var isSignIn = false

    private val _dontAskCheckboxValue = MutableStateFlow(false)
    val dontAskCheckboxValue: StateFlow<Boolean> = _dontAskCheckboxValue.asStateFlow()

    private val signingMutex = Mutex()

    init {
        scope.launch {
            observeDontAskCheckboxValueUseCase().collect { value ->
                _dontAskCheckboxValue.value = value
            }
        }
    }

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

            is AuthenticationUIEvent.SaveDontAskGuestDialogValue -> {
                Logger.d("Saving new value in VM for dontAsk: ${event.dontAskGuestDialog}")
                scope.launch {
                    setDontAskGuestDialogUseCase(dontAsk = event.dontAskGuestDialog)
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

            AuthenticationUIEvent.SetAccountAsGuest -> {
                Logger.d("Setting account as Guest inside SignInViewModel")
                scope.launch {
                    setCurrentUserProfileUuidUseCase()
                        .onSuccess {
                            eventHandler.sendEvent(UiEvent.NavigateToRoute("movies"))
                        }
                        .onFailure { error ->
                            eventHandler.sendEvent(
                                UiEvent.ShowSnackBar(
                                    defaultErrorMessageMapper.toUIText(
                                        error
                                    )
                                )
                            )
                        }
                }
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

            AuthenticationUIEvent.OnScreenChanged -> _formDataFlow.update { SignInFormStateModel() }
            else -> Unit
        }
    }

    private fun validateInput() {
        val emailResult = authFormValidationUseCases.validateEmail(formDataFlow.value.email)
        val passwordResult =
            authFormValidationUseCases.validatePassword(formDataFlow.value.password)
        val hasError = listOf(emailResult, passwordResult).any { !it.successful }

        _formDataFlow.update { it.copy(submit = !hasError) }
    }

    private fun validateEmailReset(emailReset: String): Boolean =
        authFormValidationUseCases.validateEmail(emailReset).successful

    private suspend fun signInUnified(params: UnifiedSignInUseCase.Params) {
        _signInState.update { UiState.Loading }

        unifiedSignInUseCase(params)
            .onSuccess { result ->
                Logger.d("Signed in with uid:${result.uid}")
                setCurrentUserProfileUuidUseCase(result.uid)
                _signInState.update { UiState.Success(Unit) }
                eventHandler.sendEvent(UiEvent.NavigateToRoute("movies"))
            }
            .onFailure { error ->
                _signInState.update { UiState.Success(Unit) }
                eventHandler.sendEvent(UiEvent.ShowSnackBar(defaultErrorMessageMapper.toUIText(error)))
            }
    }

    private suspend fun launchSocialSignIn(
        providerType: ProviderType,
        fetchedCredential: AuthCredential? = null
    ) {
        if (!signingMutex.tryLock()) return

        try {
            val client = socialClients[providerType]
            if (fetchedCredential != null) {
                signInUnified(
                    UnifiedSignInUseCase.Params.SocialProvider(
                        providerType = providerType,
                        credentialProvider = { fetchedCredential }
                    )
                )
            } else if (client != null) {
                signInUnified(
                    UnifiedSignInUseCase.Params.SocialProvider(
                        providerType = providerType,
                        credentialProvider = { client.getCredential() }
                    )
                )
            } else {
                eventHandler.sendEvent(UiEvent.ShowSnackBar(UIText.Plain("Unable to get credentials")))
            }
        } finally {
            signingMutex.unlock()
        }
    }

    private suspend fun resetPassword() {
        if (!formDataFlow.value.submitEmailReset) return
        _signInState.update { UiState.Loading }

        resetPasswordUseCase(Parameters.StringParam(formDataFlow.value.emailReset))
            .onFailure { error ->
                _signInState.update { UiState.Success(Unit) }
                eventHandler.sendEvent(UiEvent.ShowSnackBar(defaultErrorMessageMapper.toUIText(error)))
            }
            .onSuccess {
                _signInState.update { UiState.Success(Unit) }
                eventHandler.sendEvent(UiEvent.CloseDialog("reset_password"))
            }
    }

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }
}
