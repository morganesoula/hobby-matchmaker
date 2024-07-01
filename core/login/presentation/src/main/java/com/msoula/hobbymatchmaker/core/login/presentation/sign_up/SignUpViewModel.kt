package com.msoula.hobbymatchmaker.core.login.presentation.sign_up

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.CreateUserError
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.common.onEach
import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider
import com.msoula.hobbymatchmaker.core.login.domain.use_cases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.R
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.clearAll
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.updateStateHandle
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.sign_up.models.SignUpStateModel
import com.msoula.hobbymatchmaker.core.navigation.contracts.SignUpNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val loginValidateFormUseCase: LoginValidateFormUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val resourceProvider: StringResourcesProvider,
    private val savedStateHandle: SavedStateHandle,
    private val signUpNavigation: SignUpNavigation
) : ViewModel() {

    private val savedStateHandleKey: String = "signUpState"
    val formDataFlow = savedStateHandle.getStateFlow(savedStateHandleKey, SignUpStateModel())
    val signUpCircularProgress = MutableStateFlow(false)

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
            is AuthenticationUIEvent.OnEmailChanged -> {
                _email.value = event.email
                updateFormState {
                    it.copy(
                        email = event.email.trimEnd()
                    )
                }
            }

            is AuthenticationUIEvent.OnFirstNameChanged -> {
                _firstName.value = event.firstName
                updateFormState {
                    it.copy(
                        firstName = event.firstName
                    )
                }
            }

            is AuthenticationUIEvent.OnPasswordChanged -> {
                _password.value = event.password
                updateFormState {
                    it.copy(
                        password = event.password
                    )
                }
            }

            AuthenticationUIEvent.OnSignUp -> launchSignUp()
            else -> Unit
        }
    }

    private fun validateInput() {
        val formState = formDataFlow.value

        val emailResult = loginValidateFormUseCase.validateEmail(formState.email)
        val passwordResult =
            loginValidateFormUseCase.validatePassword.validatePassword(formState.password)
        val firstNameResult =
            loginValidateFormUseCase.validateFirstName(formState.firstName.trimEnd())

        val results = listOf(emailResult, passwordResult, firstNameResult)
        val hasError = results.any { !it.successful }

        updateFormState {
            it.copy(
                submit = !hasError,
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
            .mapSuccess {
                savedStateHandle.clearAll<SignUpStateModel>()
                signUpNavigation.redirectToAppScreen()
            }
            .mapError { error ->
                Log.e("HMM", "Could not create an account with error: $error")
                val errorMessageToDisplay: String = when (error) {
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

                updateFormState { it.copy(signUpError = errorMessageToDisplay) }
                error
            }
    }

    fun redirectToSignInScreen() = signUpNavigation.redirectToSignInScreen()
    private fun abortCircularProgress() = signUpCircularProgress.update { false }
}
