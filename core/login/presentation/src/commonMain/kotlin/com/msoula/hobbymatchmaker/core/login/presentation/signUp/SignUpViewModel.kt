package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.NavigationDestination
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.models.SignUpStateModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SignUpViewModel(
    private val validation: LoginValidateFormUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val defaultErrorMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    private val scope = externalScope ?: viewModelScope

    private val eventHandler = EventHandler()
    val events = eventHandler.events

    val formDataFlow: StateFlow<SignUpStateModel>
        field = MutableStateFlow<SignUpStateModel>(SignUpStateModel())

    val signUpState: StateFlow<UiState<Unit>>
        field = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))

    init {
        scope.launch {
            formDataFlow
                .debounce(250.milliseconds)
                .collect { newState -> validateInput(newState) }
        }
    }

    fun onEvent(event: AuthenticationUIEvent) {
        when (event) {
            is AuthenticationUIEvent.OnEmailChanged ->
                formDataFlow.update { it.copy(email = event.email.trim()) }

            is AuthenticationUIEvent.OnFirstNameChanged -> {
                formDataFlow.update { it.copy(firstName = event.firstName.trim()) }
            }

            is AuthenticationUIEvent.OnPasswordChanged ->
                formDataFlow.update { it.copy(password = event.password.trim()) }

            AuthenticationUIEvent.OnSignUp -> scope.launch {
                doSignIn {
                    signUpUseCase(
                        Parameters.DoubleStringParam(
                            formDataFlow.value.email,
                            formDataFlow.value.password
                        )
                    ).mapSuccess {}
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

        val firstNameValidation = validation.validateFirstName(firstName)
        val valid = validation.validateEmail(email).successful &&
            validation.validatePassword(password).successful && firstNameValidation.successful

        formDataFlow.update {
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
        signUpState.update { UiState.Loading }

        action()
            .onSuccess {
                signUpState.update { UiState.Success(Unit) }
                eventHandler.sendEvent(UiEvent.Navigate(NavigationDestination.Movies))
            }
            .onFailure { error ->
                signUpState.update { UiState.Error(defaultErrorMessageMapper.toUIText(error)) }
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultErrorMessageMapper.toUIText(error))
                )
            }
    }

    private fun resetForm() = formDataFlow.update { SignUpStateModel() }

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }
}
