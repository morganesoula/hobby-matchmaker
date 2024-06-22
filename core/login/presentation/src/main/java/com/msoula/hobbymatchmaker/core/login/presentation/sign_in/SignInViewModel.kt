package com.msoula.hobbymatchmaker.core.login.presentation.sign_in

import android.util.Log
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.SignInError
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.LoginWithSocialMediaUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignInUseCase
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.common.onEach
import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider
import com.msoula.hobbymatchmaker.core.di.domain.useCase.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.R
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.clearAll
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.updateStateHandle
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.models.SignInFormStateModel
import com.msoula.hobbymatchmaker.core.navigation.contracts.SignInNavigation
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SaveAuthenticationStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authFormValidationUseCases: AuthFormValidationUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val signInUseCase: SignInUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val resourceProvider: StringResourcesProvider,
    private val saveAuthenticationStateUseCase: SaveAuthenticationStateUseCase,
    private val loginWithSocialMediaUseCase: LoginWithSocialMediaUseCase,
    private val signInNavigation: SignInNavigation
) : ViewModel() {

    private val savedStateHandleKey: String = "loginState"
    val formDataFlow = savedStateHandle.getStateFlow(savedStateHandleKey, SignInFormStateModel())
    val circularProgressLoading = MutableStateFlow(false)

    private val oneTimeEventChannel = Channel<AuthenticationEvent>()
    val oneTimeEventChannelFlow = oneTimeEventChannel.receiveAsFlow()

    val openResetDialog = MutableStateFlow(false)
    val resettingEmailSent = MutableStateFlow(false)

    fun onEvent(event: AuthenticationUIEvent) {
        when (event) {
            is AuthenticationUIEvent.OnEmailChanged -> {
                updateFormState { it.copy(email = event.email.trimEnd()) }
                validateInput()
            }

            is AuthenticationUIEvent.OnEmailResetChanged -> {
                updateFormState {
                    it.copy(
                        emailReset = event.emailReset.trimEnd(),
                        submitEmailReset = validateEmailReset(event.emailReset)
                    )
                }
            }

            is AuthenticationUIEvent.OnPasswordChanged -> {
                updateFormState { it.copy(password = event.password.trimEnd()) }
                validateInput()
            }

            AuthenticationUIEvent.OnForgotPasswordClicked -> {
                toggleResetDialog(true)
            }

            AuthenticationUIEvent.HideForgotPasswordDialog -> {
                toggleResetDialog(false)
            }

            AuthenticationUIEvent.OnResetPasswordConfirmed -> launchResetPassword()

            AuthenticationUIEvent.OnSignIn -> {
                abortCircularProgress()
                logIn()
            }

            else -> Unit
        }
    }

    fun handleFacebookLogin(credential: AuthCredential) {
        launchCircularProgress()
        handleSocialMediaLogin(
            credential,
            AuthenticationEvent::OnFacebookFailedConnection,
            "Facebook login failed"
        )
    }

    fun handleGoogleLogin(result: GetCredentialResponse?, googleAuthClient: GoogleAuthClient) {
        result?.let {
            val authCredential = googleAuthClient.handleSignIn(it)
            handleSocialMediaLogin(
                authCredential,
                AuthenticationEvent::OnGoogleFailedConnection,
                "Google login failed"
            )
        } ?: Log.e("HMM", "Could not get credentials response from UI")
    }

    private fun handleSocialMediaLogin(
        credential: AuthCredential,
        onFailureEvent: (String) -> AuthenticationEvent,
        errorMessage: String
    ) {
        viewModelScope.launch(ioDispatcher) {
            loginWithSocialMediaUseCase(credential)
                .onEach {
                    viewModelScope.launch {
                        abortCircularProgress()
                    }
                }
                .mapSuccess {
                    updateDataStoreAndRedirect()
                }
                .mapError { error ->
                    Log.e("HMM", errorMessage)
                    viewModelScope.launch {
                        sendEvent(onFailureEvent(error.message))
                    }
                    error
                }
        }
    }

    private fun updateFormState(update: (SignInFormStateModel) -> SignInFormStateModel) {
        savedStateHandle.updateStateHandle(savedStateHandleKey, update)
    }

    private suspend fun updateDataStoreAndRedirect() {
        saveAuthenticationStateUseCase(true)
        withContext(Dispatchers.Main) {
            redirectToAppScreen()
        }
    }

    private fun validateInput() {
        val emailResult = authFormValidationUseCases.validateEmailUseCase(formDataFlow.value.email)
        val passwordResult =
            authFormValidationUseCases.validatePasswordUseCase.validateLoginPassword(formDataFlow.value.password)
        val hasError = listOf(emailResult, passwordResult).any { !it.successful }

        updateFormState { it.copy(submit = !hasError) }
    }

    private fun validateEmailReset(emailReset: String): Boolean =
        authFormValidationUseCases.validateEmailUseCase(emailReset).successful

    private fun logIn() {
        viewModelScope.launch(ioDispatcher) {
            signInUseCase(formDataFlow.value.email, formDataFlow.value.password)
                .onEach {
                    viewModelScope.launch {
                        abortCircularProgress()
                    }
                }
                .mapSuccess {
                    clearFormState()
                    withContext(Dispatchers.Main) {
                        redirectToAppScreen()
                    }
                }
                .mapError { error ->
                    Log.d("HMM", "SignInViewModel - Error while signing in: ${error.message}")
                    val errorMessage: String = when (error) {
                        is SignInError.WrongPassword -> resourceProvider.getString(
                            R.string.login_error
                        )

                        is SignInError.UserNotFound -> resourceProvider.getString(
                            R.string.user_not_found_error
                        )

                        is SignInError.UserDisabled -> resourceProvider.getString(
                            R.string.user_disabled_error
                        )

                        is SignInError.TooManyRequests -> resourceProvider.getString(
                            R.string.too_many_requests_error
                        )

                        else -> error.message
                    }

                    updateFormState { it.copy(logInError = errorMessage) }
                    error
                }
        }
    }

    private fun launchResetPassword() {
        viewModelScope.launch(ioDispatcher) {
            resetPassword()
        }
    }

    private suspend fun resetPassword() {
        launchCircularProgress()

        resetPasswordUseCase(formDataFlow.value.emailReset)
            .onEach {
                viewModelScope.launch {
                    abortCircularProgress()
                }
            }
            .mapSuccess {
                if (openResetDialog.value) {
                    toggleResetDialog(false)
                    resetEmailSentValue(true)
                }
            }
            .mapError { error ->
                Log.e(
                    "HMM",
                    "Could not reset password with ${formDataFlow.value.emailReset} - $error"
                )
                sendEvent(AuthenticationEvent.OnResetPasswordFailed(resourceProvider.getString(R.string.reset_password_error)))
                resetEmailSentValue(false)
                error
            }
    }

    private fun redirectToAppScreen() {
        launchCircularProgress()

        viewModelScope.launch(ioDispatcher) {
            saveAuthenticationStateUseCase(true)
            withContext(Dispatchers.Main) {
                abortCircularProgress()
                signInNavigation.redirectToAppScreen()
            }
        }
    }

    private fun toggleResetDialog(show: Boolean) {
        openResetDialog.value = show
    }

    private fun resetEmailSentValue(reset: Boolean) {
        resettingEmailSent.value = reset
    }

    private fun clearFormState() = savedStateHandle.clearAll<SignInFormStateModel>()

    fun redirectToSignUpScreen() = signInNavigation.redirectToSignUpScreen()
    private fun launchCircularProgress() = circularProgressLoading.update { true }
    private fun abortCircularProgress() = circularProgressLoading.update { false }

    private fun sendEvent(event: AuthenticationEvent) {
        viewModelScope.launch {
            oneTimeEventChannel.send(event)
        }
    }
}
