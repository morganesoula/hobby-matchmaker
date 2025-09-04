package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpErrors
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.data.ValidationResult
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidatePasswordUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.fakes.FakeSignUpErrorMessageProvider
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest : FunSpec({
    val dispatcher = StandardTestDispatcher()

    val validateEmailUseCase = mockk<ValidateEmailUseCase>()
    val validateNameUseCase = mockk<ValidateNameUseCase>()
    val validatePasswordUseCase = mockk<ValidatePasswordUseCase>()
    val loginValidateFormUseCase = LoginValidateFormUseCase(
        validateEmailUseCase, validatePasswordUseCase, validateNameUseCase
    )
    val signUpUseCase = mockk<SignUpUseCase>()
    val errorMessageProvider = FakeSignUpErrorMessageProvider()

    afterTest {
        clearAllMocks()
    }

    context("onEvent") {
        test("should update email when onEmailChanged called") {
            val signUpVM = SignUpViewModel(
                loginValidateFormUseCase, signUpUseCase, errorMessageProvider, dispatcher
            )

            signUpVM.formDataFlow.value.email shouldBe ""
            signUpVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test@test.fr"))
            signUpVM.formDataFlow.value.email shouldBe "test@test.fr"
        }

        test("should update password when onPasswordChanged called") {
            val signUpVM = SignUpViewModel(
                loginValidateFormUseCase, signUpUseCase, errorMessageProvider, dispatcher
            )

            signUpVM.formDataFlow.value.password shouldBe ""
            signUpVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("123456"))
            signUpVM.formDataFlow.value.password shouldBe "123456"
        }

        test("should update firstname when onNameChanged called") {
            val signUpVM = SignUpViewModel(
                loginValidateFormUseCase, signUpUseCase, errorMessageProvider, dispatcher
            )

            signUpVM.formDataFlow.value.firstName shouldBe ""
            signUpVM.onEvent(AuthenticationUIEvent.OnFirstNameChanged("testName"))
            signUpVM.formDataFlow.value.firstName shouldBe "testName"
        }

        test("should call SignupEvent.Success on onSignUp success") {
            runTest {
                val testDispatcher = UnconfinedTestDispatcher(testScheduler)
                Dispatchers.setMain(testDispatcher)

                every { validateEmailUseCase(any()) } returns ValidationResult(true)
                every { validatePasswordUseCase.validatePassword(any()) } returns
                    ValidationResult(true)
                every { validateNameUseCase(any()) } returns ValidationResult(true)
                every { signUpUseCase(any()) } returns flowOf(Result.Success(SignUpSuccess("uid")))

                val signUpVM = SignUpViewModel(
                    loginValidateFormUseCase, signUpUseCase, errorMessageProvider, testDispatcher
                )

                try {
                    signUpVM.onEvent(AuthenticationUIEvent.OnFirstNameChanged("TestNom"))
                    signUpVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test@test.fr"))
                    signUpVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("123456"))

                    signUpVM.onEvent(AuthenticationUIEvent.OnSignUp)
                    advanceUntilIdle()

                    signUpVM.signUpState.value shouldBe SignUpEvent.Success
                } finally {
                    Dispatchers.resetMain()
                }
            }
        }

        test("should call SignUpEvent.Failure on onSignUp failure") {
            runTest {
                val testDispatcher = UnconfinedTestDispatcher(testScheduler)
                Dispatchers.setMain(testDispatcher)

                every { validateEmailUseCase(any()) } returns ValidationResult(true)
                every { validatePasswordUseCase.validatePassword(any()) } returns
                    ValidationResult(true)
                every { validateNameUseCase(any()) } returns ValidationResult(true)
                every { signUpUseCase(any()) } returns flowOf(
                    Result.Failure(
                        SignUpErrors.UnknownErrorHMM(
                            "Unknown error oopsie"
                        )
                    )
                )

                val signUpVM = SignUpViewModel(
                    loginValidateFormUseCase, signUpUseCase, errorMessageProvider, testDispatcher
                )

                try {
                    signUpVM.onEvent(AuthenticationUIEvent.OnFirstNameChanged("TestNom"))
                    signUpVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test@test.fr"))
                    signUpVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("123456"))

                    signUpVM.onEvent(AuthenticationUIEvent.OnSignUp)
                    advanceUntilIdle()

                    signUpVM.isLoading.value shouldBe false
                    signUpVM.signUpState.value shouldBe SignUpEvent.Error("Unknown error oopsie")
                } finally {
                    Dispatchers.resetMain()
                }
            }
        }

        test("should call SignUpEvent.Loading on onSignUp loading") {
            runTest {
                val testDispatcher = UnconfinedTestDispatcher(testScheduler)
                Dispatchers.setMain(testDispatcher)

                every { validateEmailUseCase(any()) } returns ValidationResult(true)
                every { validatePasswordUseCase.validatePassword(any()) } returns
                    ValidationResult(true)
                every { validateNameUseCase(any()) } returns ValidationResult(true)
                every { signUpUseCase(any()) } returns flowOf(Result.Loading)

                val signUpVM = SignUpViewModel(
                    loginValidateFormUseCase, signUpUseCase, errorMessageProvider, testDispatcher
                )

                try {
                    signUpVM.onEvent(AuthenticationUIEvent.OnFirstNameChanged("TestNom"))
                    signUpVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test@test.fr"))
                    signUpVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("123456"))

                    signUpVM.onEvent(AuthenticationUIEvent.OnSignUp)
                    advanceUntilIdle()

                    signUpVM.isLoading.value shouldBe true
                    signUpVM.signUpState.value shouldBe SignUpEvent.Loading
                } finally {
                    Dispatchers.resetMain()
                }
            }
        }

        test("should enable submit when all values are good") {
            val signUpVM = SignUpViewModel(
                loginValidateFormUseCase, signUpUseCase, errorMessageProvider, dispatcher
            )

            signUpVM.formDataFlow.value.submit shouldBe false

            every { validateEmailUseCase(any()) } returns ValidationResult(true)
            every { validatePasswordUseCase.validatePassword(any()) } returns
                ValidationResult(true)
            every { validateNameUseCase(any()) } returns ValidationResult(true)

            signUpVM.validateInput(signUpVM.formDataFlow.value)
            signUpVM.formDataFlow.value.submit shouldBe true
        }

        test("should not enable submit when one validation fails") {
            val signUpVM = SignUpViewModel(
                loginValidateFormUseCase, signUpUseCase, errorMessageProvider, dispatcher
            )

            every { validateEmailUseCase(any()) } returns ValidationResult(true)
            every { validatePasswordUseCase.validatePassword(any()) } returns
                ValidationResult(false)
            every { validateNameUseCase(any()) } returns ValidationResult(true)

            signUpVM.validateInput(signUpVM.formDataFlow.value)
            signUpVM.formDataFlow.value.submit shouldBe false
        }
    }

    context("validateInput") {
        test("should display first name error message when invalid") {
            val signUpVM = SignUpViewModel(
                loginValidateFormUseCase, signUpUseCase, errorMessageProvider, dispatcher
            )

            every { validateEmailUseCase(any()) } returns ValidationResult(true)
            every { validatePasswordUseCase.validatePassword(any()) } returns
                ValidationResult(true)
            every { validateNameUseCase(any()) } returns ValidationResult(
                false,
                "First name too short"
            )

            signUpVM.onEvent(AuthenticationUIEvent.OnFirstNameChanged("T"))
            signUpVM.validateInput(signUpVM.formDataFlow.value)

            signUpVM.formDataFlow.value.signUpError shouldBe "First name too short"
        }

        test("should trim email and first name before validation") {
            val signUpVM = SignUpViewModel(
                loginValidateFormUseCase, signUpUseCase, errorMessageProvider, dispatcher
            )

            every { validateEmailUseCase(any()) } returns ValidationResult(true)
            every { validateNameUseCase(any()) } returns ValidationResult(true)
            every { validatePasswordUseCase.validatePassword("") } returns
                ValidationResult(true)

            signUpVM.onEvent(AuthenticationUIEvent.OnEmailChanged("   test@test.fr   "))
            signUpVM.onEvent(AuthenticationUIEvent.OnFirstNameChanged("   Jean   "))

            signUpVM.validateInput(signUpVM.formDataFlow.value)

            verify { validateEmailUseCase("test@test.fr") }
            verify { validateNameUseCase("Jean") }
        }
    }
})
