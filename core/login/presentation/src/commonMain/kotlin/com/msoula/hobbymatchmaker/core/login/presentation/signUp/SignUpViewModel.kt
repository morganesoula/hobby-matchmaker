package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.core.login.presentation.interactors.SignUpInteractor
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.models.SignUpStateModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SignUpViewModel(
    private val signUpInteractor: SignUpInteractor,
    private val defaultErrorMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    private val scope = externalScope ?: viewModelScope

    private val eventHandler = EventHandler()
    val events = eventHandler.events

    private val _formDataFlow = MutableStateFlow(SignUpStateModel())
    val formDataFlow = _formDataFlow.asStateFlow()

    private val _signUpState: MutableStateFlow<UiState<Unit>> =
        MutableStateFlow(UiState.Success(Unit))
    val signUpState = _signUpState.asStateFlow()

    init {
        scope.launch {
            formDataFlow
                .debounce(250.milliseconds)
                .collectLatest { newState ->
                    validateInput(newState)
                }
        }
    }

    fun onEvent(event: AuthenticationUIEvent) {
        when (event) {
            is AuthenticationUIEvent.OnEmailChanged ->
                _formDataFlow.update { it.copy(email = event.email.trim()) }

            is AuthenticationUIEvent.OnFirstNameChanged -> {
                _formDataFlow.update { it.copy(firstName = event.firstName.trim()) }
            }

            is AuthenticationUIEvent.OnPasswordChanged ->
                _formDataFlow.update { it.copy(password = event.password.trim()) }

            AuthenticationUIEvent.OnSignUp -> scope.launch {
                doSignIn {
                    signUpInteractor.createAccount(
                        formDataFlow.value.email,
                        formDataFlow.value.password
                    )
                }
            }

            AuthenticationUIEvent.OnScreenChanged -> resetForm()

            else -> Unit
        }
    }

    @VisibleForTesting
    internal fun validateInput(formState: SignUpStateModel) {
        val email = formState.email
        val password = formState.password
        val firstName = formState.firstName

        val firstNameValidation = signUpInteractor.validateFirstName(firstName)
        val valid = signUpInteractor
            .validateCredentials(email, password) && firstNameValidation.successful

        _formDataFlow.update {
            it.copy(
                submit = valid,
                signUpError = if (formState.firstName.isNotEmpty()) firstNameValidation.errorMessage
                    ?: "" else ""
            )
        }
    }

    private suspend fun doSignIn(
        action: suspend () -> AppResult<Unit, AppError>
    ) {
        _signUpState.update { UiState.Loading }

        action()
            .onSuccess {
                _signUpState.update { UiState.Success(Unit) }
                eventHandler.sendEvent(UiEvent.NavigateToRoute("movies"))
            }
            .onFailure { error ->
                _signUpState.update { UiState.Success(Unit) }
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultErrorMessageMapper.toUIText(error))
                )
            }
    }

    private fun resetForm() = _formDataFlow.update { SignUpStateModel() }

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }
}
