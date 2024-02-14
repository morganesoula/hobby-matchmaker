package com.msoula.auth.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.msoula.auth.R
import com.msoula.auth.data.AuthUIEvent
import com.msoula.auth.data.SignUpRegistrationState
import com.msoula.auth.domain.repository.AuthRepository
import com.msoula.auth.domain.repository.Response
import com.msoula.di.domain.StringResourcesProvider
import com.msoula.di.domain.useCase.AuthFormValidationUseCase
import com.msoula.di.navigation.HomeScreenRoute
import com.msoula.di.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel
@Inject
constructor(
    private val authFormValidationUseCases: AuthFormValidationUseCase,
    private val authRepository: AuthRepository,
    private val resourceProvider: StringResourcesProvider,
    private val ioDispatcher: CoroutineDispatcher,
    private val navigator: Navigator,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val savedStateHandleKey: String = "signUpState"
    val formDataFlow = savedStateHandle.getStateFlow(savedStateHandleKey, SignUpRegistrationState())
    val signUpCircularProgress = MutableStateFlow(false)

    fun onEvent(event: AuthUIEvent) {
        when (event) {
            is AuthUIEvent.OnEmailChanged -> {
                savedStateHandle.updateStateHandle<SignUpRegistrationState>(savedStateHandleKey) {
                    it.copy(
                        email = event.email.trimEnd()
                    )
                }
                validateInput()
            }

            is AuthUIEvent.OnFirstNameChanged -> {
                savedStateHandle.updateStateHandle<SignUpRegistrationState>(savedStateHandleKey) {
                    it.copy(
                        firstName = event.firstName.trimEnd()
                    )
                }
                validateInput()
            }

            is AuthUIEvent.OnLastNameChanged -> {
                savedStateHandle.updateStateHandle<SignUpRegistrationState>(savedStateHandleKey) {
                    it.copy(
                        lastName = event.lastName.trimEnd()
                    )
                }
                validateInput()
            }

            is AuthUIEvent.OnPasswordChanged -> {
                savedStateHandle.updateStateHandle<SignUpRegistrationState>(savedStateHandleKey) {
                    it.copy(
                        password = event.password
                    )
                }
                validateInput()
            }

            AuthUIEvent.OnSignUp -> launchSignUp()
            else -> Unit
        }
    }

    private fun validateInput() {
        val emailResult = authFormValidationUseCases.validateEmail(formDataFlow.value.email)
        val passwordResult =
            authFormValidationUseCases.validatePassword.validatePassword(formDataFlow.value.password)
        val firstNameResult =
            authFormValidationUseCases.validateFirstName(formDataFlow.value.firstName.trimEnd())
        val lastNameResult =
            authFormValidationUseCases.validateLastName(formDataFlow.value.lastName.trimEnd())

        val error = listOf(
            emailResult,
            passwordResult,
            firstNameResult,
            lastNameResult,
        ).any { !it.successful }

        if (error) {
            savedStateHandle.updateStateHandle<SignUpRegistrationState>(savedStateHandleKey) {
                it.copy(
                    submit = false
                )
            }
            return
        } else {
            savedStateHandle.updateStateHandle<SignUpRegistrationState>(savedStateHandleKey) {
                it.copy(
                    submit = true
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
        when (
            val result =
                authRepository.signUp(
                    formDataFlow.value.email,
                    formDataFlow.value.password,
                )
        ) {
            is Response.Success -> {
                signUpCircularProgress.value = false
                savedStateHandle.clearAll<SignUpRegistrationState>()

                navigator.navigate(HomeScreenRoute)
            }

            is Response.Failure -> {
                when (val exception = result.exception) {
                    is FirebaseAuthUserCollisionException -> {
                        signUpCircularProgress.value = false
                        savedStateHandle.updateStateHandle<SignUpRegistrationState>(
                            savedStateHandleKey
                        ) {
                            it.copy(
                                signUpError =
                                resourceProvider.getString(
                                    R.string.signup_error,
                                )
                            )
                        }
                        Log.e("HMM", "Email already associated")
                    }

                    else -> {
                        signUpCircularProgress.value = false
                        savedStateHandle.updateStateHandle<SignUpRegistrationState>(
                            savedStateHandleKey
                        ) {
                            it.copy(
                                signUpError = exception.message
                            )
                        }
                        Log.e("HMM", "Could not create an account")
                    }
                }
            }
        }
    }
}
