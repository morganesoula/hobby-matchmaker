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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val firstNameFlow = MutableStateFlow(SignUpRegistrationState().firstName.trimEnd())
    private val lastNameFlow = MutableStateFlow(SignUpRegistrationState().lastName.trimEnd())
    private val emailFlow = MutableStateFlow(SignUpRegistrationState().email.trimEnd())
    private val passwordFlow = MutableStateFlow(SignUpRegistrationState().password.trimEnd())
    private val signUpErrorFlow = MutableStateFlow(SignUpRegistrationState().signUpError)

    private val formDataFlow = MutableStateFlow(SignUpRegistrationState())

    val registrationFormState = formDataFlow.map { formData ->
        SignUpRegistrationState(
            formData.firstName,
            formData.lastName,
            formData.email,
            formData.password,
            submit = validateInput(formData),
            signUpError = formData.signUpError
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SignUpRegistrationState())

    val signUpCircularProgress = MutableStateFlow(false)

    fun onEvent(event: AuthUIEvent) {
        when (event) {
            is AuthUIEvent.OnEmailChanged -> formDataFlow.update { it.copy(email = event.email) }
            is AuthUIEvent.OnFirstNameChanged -> formDataFlow.update { it.copy(firstName = event.firstName) }
            is AuthUIEvent.OnLastNameChanged -> formDataFlow.update { it.copy(lastName = event.lastName) }
            is AuthUIEvent.OnPasswordChanged -> formDataFlow.update { it.copy(password = event.password) }
            AuthUIEvent.OnSignUp -> launchSignUp()
            else -> Unit
        }
    }

    private fun validateInput(formData: SignUpRegistrationState): Boolean {
        val emailResult = authFormValidationUseCases.validateEmail(formData.email)
        val passwordResult =
            authFormValidationUseCases.validatePassword.validatePassword(formData.password)
        val firstNameResult = authFormValidationUseCases.validateFirstName(formData.firstName)
        val lastNameResult = authFormValidationUseCases.validateLastName(formData.lastName)

        // TODO Check here
        return listOf(
            emailResult,
            passwordResult,
            firstNameResult,
            lastNameResult
        ).all { it.successful }
    }

    private fun launchSignUp() {
        viewModelScope.launch(ioDispatcher) {
            createFirebaseAccount()
        }
    }

    private suspend fun createFirebaseAccount() {
        signUpCircularProgress.value = true

        when (val result = authRepository.signUp(
            emailFlow.value, passwordFlow.value
        )) {
            is Response.Success -> {
                signUpCircularProgress.value = false
                navigator.navigate(HomeScreenRoute)
            }

            is Response.Failure -> {
                when (result.exception) {
                    is FirebaseAuthUserCollisionException -> {
                        signUpCircularProgress.value = false
                        signUpErrorFlow.update {
                            resourceProvider.getString(
                                R.string.signup_error
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