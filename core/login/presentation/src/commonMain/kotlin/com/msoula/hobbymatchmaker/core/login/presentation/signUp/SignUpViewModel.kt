package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpErrors
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.Res
import com.msoula.hobbymatchmaker.core.login.presentation.connection_issue
import com.msoula.hobbymatchmaker.core.login.presentation.email_already_exists_error
import com.msoula.hobbymatchmaker.core.login.presentation.internal_error
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.models.SignUpStateModel
import com.msoula.hobbymatchmaker.core.login.presentation.too_many_requests_error
import com.msoula.hobbymatchmaker.core.login.presentation.user_disabled_error
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SignUpViewModel(
    private val loginValidateFormUseCase: LoginValidateFormUseCase,
    private val signUpUseCase: SignUpUseCase
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
                _formDataFlow.update { it.copy(email = event.email.trimEnd()) }

            is AuthenticationUIEvent.OnFirstNameChanged -> {
                _formDataFlow.update { it.copy(firstName = event.firstName) }
            }

            is AuthenticationUIEvent.OnPasswordChanged ->
                _formDataFlow.update { it.copy(password = event.password) }

            AuthenticationUIEvent.OnSignUp -> createFirebaseAccount()
            else -> Unit
        }
    }

    private fun validateInput(formState: SignUpStateModel) {
        val emailResult = loginValidateFormUseCase.validateEmail(formState.email)
        val passwordResult =
            loginValidateFormUseCase.validatePassword.validatePassword(formState.password)
        val firstNameResult =
            loginValidateFormUseCase.validateFirstName(formState.firstName.trimEnd())

        val results = listOf(emailResult, passwordResult, firstNameResult).any { !it.successful }

        _formDataFlow.update {
            it.copy(
                submit = !results,
                signUpError = if (formState.firstName.isNotEmpty()) firstNameResult.errorMessage
                    ?: "" else ""
            )
        }
    }

    private fun createFirebaseAccount() {
        viewModelScope.launch {
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
                            SignUpEvent.Error(handleSignUpError(result.error))
                        }
                    }
                }
            }
        }
    }

    private fun handleSignUpError(error: AppError): StringResource? {
        return when (error) {
            is SignUpErrors.EmailAlreadyExists ->
                Res.string.email_already_exists_error

            is SignUpErrors.UserDisabled ->
                Res.string.user_disabled_error

            is SignUpErrors.TooManyRequests ->
                Res.string.too_many_requests_error

            is SignUpErrors.InternalError ->
                Res.string.internal_error

            is SignUpErrors.Connection ->
                Res.string.connection_issue

            else -> null
        }
    }
}
