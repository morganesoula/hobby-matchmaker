package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpErrors
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.R
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.clearAll
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.updateStateHandle
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.models.SignUpStateModel
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
    private val savedStateHandle: SavedStateHandle,
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val savedStateHandleKey: String = "signUpState"
    val formDataFlow = savedStateHandle.getStateFlow(savedStateHandleKey, SignUpStateModel())

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _signUpState: MutableStateFlow<SignUpEvent> = MutableStateFlow(SignUpEvent.Idle)
    val signUpState: StateFlow<SignUpEvent> = _signUpState.asStateFlow()

    private val _email = MutableStateFlow(savedStateHandle["email"] ?: "")
    private val _firstName = MutableStateFlow(savedStateHandle["firstName"] ?: "")
    private val _password = MutableStateFlow(savedStateHandle["password"] ?: "")

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
                    resourceProvider.getString(
                        error
                    )
                } ?: "" else ""
            )
        }
    }

    private fun updateFormState(update: (SignUpStateModel) -> SignUpStateModel) {
        savedStateHandle.updateStateHandle(savedStateHandleKey, update)
    }

    @SuppressLint("NewApi")
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
                            savedStateHandle.clearAll<SignUpStateModel>()
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
            is SignUpErrors.EmailAlreadyExists -> resourceProvider.getString(
                R.string.email_already_exists_error
            )

            is SignUpErrors.UserDisabled -> resourceProvider.getString(
                R.string.user_disabled_error
            )

            is SignUpErrors.TooManyRequests -> resourceProvider.getString(
                R.string.too_many_requests_error
            )

            is SignUpErrors.InternalError -> resourceProvider.getString(
                R.string.internal_error
            )

            else -> error.message
        }
    }
}
