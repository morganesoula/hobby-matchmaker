package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpErrors
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.StateSaver
import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.Res
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SignUpViewModel(
    private val loginValidateFormUseCase: LoginValidateFormUseCase,
    private val resourceProvider: StringResourcesProvider,
    private val stateSaver: StateSaver,
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val savedStateHandleKey: String = "signUpState"
    val formDataFlow =
        MutableStateFlow(stateSaver.getState(savedStateHandleKey, SignUpStateModel()))

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _signUpState: MutableStateFlow<SignUpEvent> = MutableStateFlow(SignUpEvent.Idle)
    val signUpState: StateFlow<SignUpEvent> = _signUpState.asStateFlow()

    private val _email = MutableStateFlow(stateSaver.getState("email", ""))
    private val _firstName = MutableStateFlow(stateSaver.getState("firstName", ""))
    private val _password = MutableStateFlow(stateSaver.getState("password", ""))

    init {
        viewModelScope.launch {
            combine(
                _email,
                _firstName,
                _password
            ) { email, firstName, password ->
                SignUpStateModel(
                    email = email,
                    firstName = firstName,
                    password = password
                )
            }
                .debounce(250.milliseconds)
                .collectLatest { validateInput() }
        }
    }

    fun onEvent(event: AuthenticationUIEvent) {
        when (event) {
            is AuthenticationUIEvent.OnEmailChanged -> handleInputChange(event.email, "email")
            is AuthenticationUIEvent.OnFirstNameChanged -> handleInputChange(
                event.firstName,
                "firstName"
            )

            is AuthenticationUIEvent.OnPasswordChanged -> handleInputChange(
                event.password,
                "password"
            )

            AuthenticationUIEvent.OnSignUp -> createFirebaseAccount()
            else -> Unit
        }
    }

    private fun handleInputChange(input: String, field: String) {
        when (field) {
            "email" -> _email.value = input.trimEnd()
            "firstName" -> _firstName.value = input
            "password" -> _password.value = input
        }

        updateFormState {
            it.copy(email = _email.value, firstName = _firstName.value, password = _password.value)
        }
    }

    private fun validateInput() {
        val formState = formDataFlow.value

        val emailResult = loginValidateFormUseCase.validateEmail(formState.email)
        val passwordResult =
            loginValidateFormUseCase.validatePassword.validatePassword(formState.password)
        val firstNameResult =
            loginValidateFormUseCase.validateFirstName(formState.firstName.trimEnd())

        val results = listOf(emailResult, passwordResult, firstNameResult).any { !it.successful }

        updateFormState {
            it.copy(
                submit = !results,
                signUpError = if (formState.firstName.isNotEmpty()) firstNameResult.errorMessage?.let { error ->
                    resourceProvider.getStringByKey(
                        error
                    )
                } ?: "" else ""
            )
        }
    }

    private fun updateFormState(update: (SignUpStateModel) -> SignUpStateModel) {
        stateSaver.updateState(savedStateHandleKey, update)
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
                        is Result.Success -> {
                            stateSaver.removeState("signUpState")
                            SignUpEvent.Success
                        }

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

    private fun handleSignUpError(error: AppError): String {
        return when (error) {
            is SignUpErrors.EmailAlreadyExists -> resourceProvider.getStringByKey(
                Res.string.email_already_exists_error.key
            )

            is SignUpErrors.UserDisabled -> resourceProvider.getStringByKey(
                Res.string.user_disabled_error.key
            )

            is SignUpErrors.TooManyRequests -> resourceProvider.getStringByKey(
                Res.string.too_many_requests_error.key
            )

            is SignUpErrors.InternalError -> resourceProvider.getStringByKey(
                Res.string.internal_error.key
            )

            else -> error.message
        }
    }
}
