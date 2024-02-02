package com.msoula.auth.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.msoula.auth.R
import com.msoula.auth.data.AuthUIEvent
import com.msoula.auth.data.SignUpRegistrationState
import com.msoula.auth.domain.repository.AuthRepository
import com.msoula.auth.domain.repository.Response
import com.msoula.di.domain.StringResourcesProvider
import com.msoula.di.domain.use_case.AuthFormValidationUseCase
import com.msoula.di.navigation.HomeScreenRoute
import com.msoula.di.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SignUpViewModel @Inject constructor(
    @Named("authRepository") private val authRepository: AuthRepository,
    @Named("authFormUseCases") private val authFormValidationUseCases: AuthFormValidationUseCase,
    @Named("resourceProvider") private val resourceProvider: StringResourcesProvider,
    @Named("ioDispatcher") private val ioDispatcher: CoroutineDispatcher,
    private val navigator: Navigator
) : ViewModel() {

    private val _registrationFormState = MutableStateFlow(SignUpRegistrationState())
    val registrationState = _registrationFormState.asStateFlow()

    val signUpCircularProgress = mutableStateOf(false)

    fun onEvent(event: AuthUIEvent) {
        when (event) {
            is AuthUIEvent.OnEmailChanged -> {
                _registrationFormState.update { it.copy(email = event.email) }
                validateInput()
            }

            is AuthUIEvent.OnFirstNameChanged -> {
                _registrationFormState.update { it.copy(firstName = event.firstName) }
                validateInput()
            }

            is AuthUIEvent.OnLastNameChanged -> {
                _registrationFormState.update { it.copy(lastName = event.lastName) }
                validateInput()
            }

            is AuthUIEvent.OnPasswordChanged -> {
                _registrationFormState.update { it.copy(password = event.password) }
                validateInput()
            }

            AuthUIEvent.OnSignUp -> launchSignUp()

            else -> Unit
        }
    }

    private fun validateInput() {
        val emailResult = authFormValidationUseCases.validateEmail(registrationState.value.email)
        val passwordResult =
            authFormValidationUseCases.validatePassword.validatePassword(registrationState.value.password)
        val firstNameResult =
            authFormValidationUseCases.validateFirstName(registrationState.value.firstName)
        val lastNameResult =
            authFormValidationUseCases.validateLastName(registrationState.value.lastName)

        val error = listOf(
            emailResult,
            passwordResult,
            firstNameResult,
            lastNameResult
        ).any { !it.successful }

        if (error) {
            if (registrationState.value.submit) _registrationFormState.update { it.copy(submit = false) }
            return
        } else {
            _registrationFormState.update { it.copy(submit = true) }
        }
    }

    private fun launchSignUp() {
        viewModelScope.launch(ioDispatcher) {
            createFirebaseAccount()
        }
    }

    private suspend fun createFirebaseAccount() {
        signUpCircularProgress.value = true

        when (val result = authRepository.signUp(
            registrationState.value.email,
            registrationState.value.password
        )) {
            is Response.Success -> {
                signUpCircularProgress.value = false
                navigator.navigate(HomeScreenRoute)
            }
            is Response.Failure -> {
                when (result.exception) {
                    is FirebaseAuthUserCollisionException -> {
                        signUpCircularProgress.value = false
                        _registrationFormState.update {
                            it.copy(
                                signUpError = resourceProvider.getString(
                                    R.string.signup_error
                                )
                            )
                        }
                        Log.e("HMM", "Email already associated")
                    }

                    else -> {
                        signUpCircularProgress.value = false
                        Log.e("HMM", "Could not create an account")
                    }
                }
            }
        }
    }
}