package com.msoula.hobbymatchmaker.core.login.presentation.sign_up

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.CreateUserError
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.common.onEach
import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider
import com.msoula.hobbymatchmaker.core.login.domain.use_cases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.R
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.clearAll
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.updateStateHandle
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.sign_up.models.SignUpStateModel
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.CreateUserUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SignUpViewModel(
    private val loginValidateFormUseCase: LoginValidateFormUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val resourceProvider: StringResourcesProvider,
    private val savedStateHandle: SavedStateHandle,
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {

    private val savedStateHandleKey: String = "signUpState"
    val formDataFlow = savedStateHandle.getStateFlow(savedStateHandleKey, SignUpStateModel())
    val signUpCircularProgress = MutableStateFlow(false)

    private val oneTimeEventChannel = Channel<AuthenticationEvent>()
    val oneTimeEventChannelFlow = oneTimeEventChannel.receiveAsFlow()

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

            AuthenticationUIEvent.OnSignUp -> launchSignUp()
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

    private fun launchSignUp() {
        viewModelScope.launch(ioDispatcher) {
            createFirebaseAccount()
        }
    }

    private suspend fun createFirebaseAccount() {
        signUpCircularProgress.value = true

        signUpUseCase(
            formDataFlow.value.email,
            formDataFlow.value.password
        )
            .onEach {
                viewModelScope.launch {
                    abortCircularProgress()
                }
            }
            .mapSuccess { uid ->
                savedStateHandle.clearAll<SignUpStateModel>()
                createUserOnline(uid)
            }
            .mapError { error ->
                Log.e("HMM", "Could not create an account with error: $error")
                val errorMessageToDisplay: String = handleSignUpError(error)
                updateFormState { it.copy(signUpError = errorMessageToDisplay) }
                error
            }
    }

    private fun abortCircularProgress() = signUpCircularProgress.update { false }

    private suspend fun createUserOnline(uid: String) {
        createUserUseCase(
            SessionUserDomainModel(
                uid = uid,
                email = formDataFlow.value.email
            )
        )
            .mapSuccess {
                oneTimeEventChannel.send(AuthenticationEvent.OnSignUpSuccess)
            }
            .mapError {
                Log.e("HMM", "VM - Could not create user online with error: $it")
                it
            }
    }

    private fun handleSignUpError(error: AppError): String {
        return when (error) {
            is CreateUserError.EmailAlreadyExists -> resourceProvider.getString(
                R.string.email_already_exists_error
            )

            is CreateUserError.UserDisabled -> resourceProvider.getString(
                R.string.user_disabled_error
            )

            is CreateUserError.TooManyRequests -> resourceProvider.getString(
                R.string.too_many_requests_error
            )

            is CreateUserError.InternalError -> resourceProvider.getString(
                R.string.internal_error
            )

            else -> error.message
        }
    }
}
