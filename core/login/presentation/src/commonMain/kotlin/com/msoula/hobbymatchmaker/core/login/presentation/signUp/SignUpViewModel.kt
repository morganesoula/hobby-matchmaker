package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthUiEventModel
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.models.SignUpStateModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SignUpViewModel(
    private val loginValidateFormUseCase: LoginValidateFormUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val defaultErrorMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    private val scope = externalScope ?: viewModelScope

    private val _formDataFlow = MutableStateFlow(SignUpStateModel())
    val formDataFlow = _formDataFlow.asStateFlow()
    private val _oneTimeEventChannel = Channel<AuthUiEventModel>()
    val oneTimeEventChannelFlow = _oneTimeEventChannel.receiveAsFlow()
    private val _signUpState: MutableStateFlow<SignUpEvent> = MutableStateFlow(SignUpEvent.Idle)
    val signUpState: StateFlow<SignUpEvent> = _signUpState.asStateFlow()

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

            AuthenticationUIEvent.OnSignUp -> createFirebaseAccount()
            AuthenticationUIEvent.OnScreenChanged -> resetForm()

            else -> Unit
        }
    }

    @VisibleForTesting
    internal fun validateInput(formState: SignUpStateModel) {
        val emailResult = loginValidateFormUseCase.validateEmail(formState.email)
        val passwordResult =
            loginValidateFormUseCase.validatePassword.validatePassword(formState.password)
        val firstNameResult =
            loginValidateFormUseCase.validateFirstName(formState.firstName)

        val results = listOf(emailResult, passwordResult, firstNameResult).any { !it.successful }

        _formDataFlow.update {
            it.copy(
                submit = !results,
                signUpError = if (formState.firstName.isNotEmpty()) firstNameResult.errorMessage
                    ?: "" else ""
            )
        }
    }

    @VisibleForTesting
    internal fun createFirebaseAccount() = scope.launch {
        _signUpState.update { SignUpEvent.Loading }

        signUpUseCase(
            Parameters.DoubleStringParam(
                formDataFlow.value.email,
                formDataFlow.value.password
            )
        )
            .onFailure { error ->
                resetSignUpState()
                val error = defaultErrorMessageMapper.toUIText(error)
                sendOnce(AuthUiEventModel.ShowError(error))
            }
            .onSuccess {
                resetSignUpState()
                sendOnce(AuthUiEventModel.OnSignUpSuccess)
            }
    }

    private fun resetForm() = _formDataFlow.update { SignUpStateModel() }
    private fun resetSignUpState() = _signUpState.update { SignUpEvent.Idle }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun sendOnce(event: AuthUiEventModel) {
        if (!_oneTimeEventChannel.isClosedForSend) {
            _oneTimeEventChannel.send(event)
        }
    }
}
