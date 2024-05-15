package com.msoula.hobbymatchmaker.core.login.presentation.sign_up

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.di.navigation.AppScreenRoute
import com.msoula.hobbymatchmaker.core.di.navigation.Navigator
import com.msoula.hobbymatchmaker.core.login.domain.use_cases.LoginFormValidationUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.clearAll
import com.msoula.hobbymatchmaker.core.login.presentation.extensions.updateStateHandle
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEventModel
import com.msoula.hobbymatchmaker.core.login.presentation.sign_up.models.SignUpStateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val loginFormValidationUseCase: LoginFormValidationUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val navigator: Navigator,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val savedStateHandleKey: String = "signUpState"
    val formDataFlow = savedStateHandle.getStateFlow(savedStateHandleKey, SignUpStateModel())
    val signUpCircularProgress = MutableStateFlow(false)

    fun onEvent(event: AuthenticationUIEventModel) {
        when (event) {
            is AuthenticationUIEventModel.OnEmailChanged -> {
                savedStateHandle.updateStateHandle<SignUpStateModel>(savedStateHandleKey) {
                    it.copy(
                        email = event.email.trimEnd(),
                    )
                }
                validateInput()
            }

            is AuthenticationUIEventModel.OnFirstNameChanged -> {
                savedStateHandle.updateStateHandle<SignUpStateModel>(savedStateHandleKey) {
                    it.copy(
                        firstName = event.firstName.trimEnd(),
                    )
                }
                validateInput()
            }

            is AuthenticationUIEventModel.OnLastNameChanged -> {
                savedStateHandle.updateStateHandle<SignUpStateModel>(savedStateHandleKey) {
                    it.copy(
                        lastName = event.lastName.trimEnd(),
                    )
                }
                validateInput()
            }

            is AuthenticationUIEventModel.OnPasswordChanged -> {
                savedStateHandle.updateStateHandle<SignUpStateModel>(savedStateHandleKey) {
                    it.copy(
                        password = event.password,
                    )
                }
                validateInput()
            }

            AuthenticationUIEventModel.OnSignUp -> launchSignUp()
            else -> Unit
        }
    }

    private fun validateInput() {
        val emailResult = loginFormValidationUseCase.validateEmail(formDataFlow.value.email)
        val passwordResult =
            loginFormValidationUseCase.validatePassword.validatePassword(formDataFlow.value.password)
        val firstNameResult =
            loginFormValidationUseCase.validateFirstName(formDataFlow.value.firstName.trimEnd())
        val lastNameResult =
            loginFormValidationUseCase.validateLastName(formDataFlow.value.lastName.trimEnd())

        val error =
            listOf(
                emailResult,
                passwordResult,
                firstNameResult,
                lastNameResult,
            ).any { !it.successful }

        if (error) {
            savedStateHandle.updateStateHandle<SignUpStateModel>(savedStateHandleKey) {
                it.copy(
                    submit = false,
                )
            }
            return
        } else {
            savedStateHandle.updateStateHandle<SignUpStateModel>(savedStateHandleKey) {
                it.copy(
                    submit = true,
                )
            }
        }
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
            .mapSuccess {
                signUpCircularProgress.value = false
                savedStateHandle.clearAll<SignUpStateModel>()

                navigator.navigate(AppScreenRoute)
            }
            .mapError { error ->
                signUpCircularProgress.value = false
                savedStateHandle.updateStateHandle<SignUpStateModel>(
                    savedStateHandleKey,
                ) {
                    it.copy(
                        signUpError = error.message,
                    )
                }

                Log.e("HMM", "Could not create an account")
                error
            }
    }
}
