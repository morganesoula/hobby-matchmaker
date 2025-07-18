package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.models.SignUpStateModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SignUpViewModel(
    private val loginValidateFormUseCase: LoginValidateFormUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val errorMessageProvider: ErrorMessageProvider,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _formDataFlow = MutableStateFlow(SignUpStateModel())
    val formDataFlow = _formDataFlow.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _signUpState: MutableStateFlow<SignUpEvent> = MutableStateFlow(SignUpEvent.Idle)
    val signUpState: StateFlow<SignUpEvent> = _signUpState.asStateFlow()

    init {
        viewModelScope.launch {
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
    internal fun createFirebaseAccount() {
        viewModelScope.launch(ioDispatcher) {
            signUpUseCase(
                Parameters.DoubleStringParam(
                    formDataFlow.value.email,
                    formDataFlow.value.password
                )
            ).collectLatest { result ->
                _signUpState.update {
                    when (result) {
                        is Result.Success -> SignUpEvent.Success

                        is Result.Loading -> {
                            _isLoading.update { true }
                            SignUpEvent.Loading
                        }

                        is Result.Failure -> {
                            _isLoading.update { false }
                            val errorMessage = handleSignUpError(result.error)
                            SignUpEvent.Error(errorMessage)
                        }
                    }
                }
            }
        }
    }

    private suspend fun handleSignUpError(error: AppError): String =
        errorMessageProvider.getMessage(error)
}
