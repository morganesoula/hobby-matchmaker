package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.NavigationDestination
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.orchestrators.SignInOrchestrator
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.models.SignInFormStateModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.models.SocialClientsVM
import dev.gitlive.firebase.auth.AuthCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInInteractor: SignInOrchestrator,
    private val socialClients: SocialClientsVM,
    private val defaultErrorMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope

    // Credential refused because the email belongs to another provider: linked after the next successful sign-in
    private var pendingLink: PendingLink? = null
    private val eventHandler = EventHandler()
    val events = eventHandler.events

    val formDataFlow: StateFlow<SignInFormStateModel>
        field = MutableStateFlow<SignInFormStateModel>(SignInFormStateModel())

    val signInState: StateFlow<UiState<Unit>>
        field = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))

    val dontAskCheckboxValue: StateFlow<Boolean> =
        signInInteractor.observeDontAsk()
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    fun onEvent(event: AuthenticationUIEvent) {
        when (event) {
            is AuthenticationUIEvent.OnEmailChanged -> {
                formDataFlow.update { it.copy(email = event.email.trimEnd()) }
                validateInput()
            }

            is AuthenticationUIEvent.OnEmailResetChanged -> {
                formDataFlow.update {
                    it.copy(
                        emailReset = event.emailReset.trimEnd(),
                        submitEmailReset = validateEmailReset(event.emailReset)
                    )
                }
            }

            is AuthenticationUIEvent.OnPasswordChanged -> {
                formDataFlow.update { it.copy(password = event.password.trimEnd()) }
                validateInput()
            }

            is AuthenticationUIEvent.SaveDontAskGuestDialogValue -> {
                scope.launch {
                    signInInteractor.setDontAsk(event.dontAskGuestDialog)
                }
            }

            AuthenticationUIEvent.OnGoogleButtonClicked ->
                handleSocialSignIn(ProviderType.GOOGLE)

            AuthenticationUIEvent.OnAppleButtonClicked ->
                handleSocialSignIn(ProviderType.APPLE)

            is AuthenticationUIEvent.OnFacebookButtonClicked -> scope.launch {
                signInSocial(ProviderType.FACEBOOK, event.credential)
            }

            AuthenticationUIEvent.OnResetPasswordConfirmed -> {
                scope.launch { resetPassword() }
            }

            AuthenticationUIEvent.SetAccountAsGuest -> scope.launch {
                doSignIn(linkPending = false) {
                    signInInteractor.signInAsGuest()
                }
            }

            AuthenticationUIEvent.OnSignIn -> scope.launch {
                doSignIn {
                    signInInteractor.signInEmail(
                        formDataFlow.value.email,
                        formDataFlow.value.password
                    )
                }
            }

            AuthenticationUIEvent.OnScreenChanged -> formDataFlow.update { SignInFormStateModel() }
            else -> Unit
        }
    }

    private fun handleSocialSignIn(provider: ProviderType) {
        scope.launch {
            val client = socialClients.clients[provider]
            if (client == null) {
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultErrorMessageMapper.toUIText(AppError.Authentication.Unknown))
                )
                return@launch
            }

            when (val credentialResult = client.getCredential()) {
                is AppResult.Failure -> eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultErrorMessageMapper.toUIText(credentialResult.error))
                )
                is AppResult.Success -> signInSocial(provider, credentialResult.data)
            }
        }
    }

    private fun validateInput() {
        val email = formDataFlow.value.email
        val password = formDataFlow.value.password
        val valid = signInInteractor.validateCredentials(email, password)

        formDataFlow.update { it.copy(submit = valid) }
    }

    private fun validateEmailReset(emailReset: String): Boolean =
        signInInteractor.validateEmail(emailReset)

    private suspend fun resetPassword() {
        if (!formDataFlow.value.submitEmailReset) return
        signInState.update { UiState.Loading }

        signInInteractor.resetPassword(formDataFlow.value.emailReset)
            .onSuccess {
                signInState.update { UiState.Success(Unit) }
                eventHandler.sendEvent(UiEvent.CloseDialog("reset_password"))
            }
            .onFailure { error ->
                signInState.update { UiState.Success(Unit) }
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(
                        defaultErrorMessageMapper.toUIText(
                            error
                        )
                    )
                )
            }
    }

    private suspend fun signInSocial(provider: ProviderType, credential: AuthCredential?) {
        doSignIn {
            signInInteractor.signInSocial(provider) { credential }
                .onFailure { error ->
                    if (error == AppError.Authentication.AccountExistsWithDifferentProvider && credential != null) {
                        pendingLink = PendingLink(provider, credential)
                    }
                }
        }
    }

    private suspend fun linkPendingCredential() {
        val pending = pendingLink ?: return
        pendingLink = null

        signInInteractor.linkCredential(pending.provider) { pending.credential }
            .onFailure { Logger.w("Linking ${pending.provider} after sign-in failed: $it") }
    }

    private suspend fun doSignIn(
        linkPending: Boolean = true,
        action: suspend () -> AppResult<Boolean, AppError>
    ) {
        signInState.update { UiState.Loading }

        action()
            .onSuccess { needsPseudo ->
                if (linkPending) linkPendingCredential()
                val destination =
                    if (needsPseudo) NavigationDestination.PickPseudo else NavigationDestination.Movies
                eventHandler.sendEvent(UiEvent.Navigate(destination))
            }
            .onFailure { error ->
                signInState.update { UiState.Success(Unit) }
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultErrorMessageMapper.toUIText(error))
                )
            }
    }

    private data class PendingLink(val provider: ProviderType, val credential: AuthCredential)

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }
}
