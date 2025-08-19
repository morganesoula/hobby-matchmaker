package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.UnifiedSignInUseCase
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.di.data.ValidationResult
import com.msoula.hobbymatchmaker.core.di.domain.useCases.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidatePasswordUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.ResetPasswordEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes.FakeSignInErrorMessageProvider
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
class SignInViewModelTest : FunSpec({
    val dispatcher = StandardTestDispatcher()

    val validatePasswordUseCase = mockk<ValidatePasswordUseCase>()
    val validateEmailUseCase = mockk<ValidateEmailUseCase>()
    val validateNameUseCase = mockk<ValidateNameUseCase>()
    val authFormValidationUseCase = AuthFormValidationUseCase(
        validateEmailUseCase = validateEmailUseCase,
        validatePasswordUseCase = validatePasswordUseCase,
        validateFirstNameUseCase = validateNameUseCase,
        validateLastNameUseCase = validateNameUseCase
    )

    val resetPasswordUseCase = mockk<ResetPasswordUseCase>()
    val unifiedSignInUseCase = mockk<UnifiedSignInUseCase>()
    val socialClients = mapOf<ProviderType, SocialUIClient>()

    afterTest {
        clearAllMocks()
    }

    context("onEvent") {
        test("should update email with new value") {
            val signInVM = SignInViewModel(
                authFormValidationUseCase, resetPasswordUseCase,
                unifiedSignInUseCase, socialClients, dispatcher,
                FakeSignInErrorMessageProvider()
            )

            every { validateEmailUseCase(any()) } returns ValidationResult(true)
            every { validatePasswordUseCase.validateLoginPassword(any()) } returns ValidationResult(
                true
            )

            signInVM.formDataFlow.value.email shouldBe ""
            signInVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test@test.fr"))
            signInVM.formDataFlow.value.email shouldBe "test@test.fr"
            signInVM.formDataFlow.value.submit shouldBe true
        }

        test("should update password with new value") {
            val signInVM = SignInViewModel(
                authFormValidationUseCase, resetPasswordUseCase,
                unifiedSignInUseCase, socialClients, dispatcher,
                FakeSignInErrorMessageProvider()
            )

            every { validateEmailUseCase(any()) } returns ValidationResult(true)
            every { validatePasswordUseCase.validateLoginPassword(any()) } returns ValidationResult(
                true
            )

            signInVM.formDataFlow.value.password shouldBe ""
            signInVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("newPassword"))
            signInVM.formDataFlow.value.password shouldBe "newPassword"
            signInVM.formDataFlow.value.submit shouldBe true
        }

        test("should reset email with new value") {
            val signInVM = SignInViewModel(
                authFormValidationUseCase, resetPasswordUseCase,
                unifiedSignInUseCase, socialClients, dispatcher,
                FakeSignInErrorMessageProvider()
            )

            every { validateEmailUseCase(any()) } returns ValidationResult(true)

            signInVM.formDataFlow.value.emailReset shouldBe ""
            signInVM.onEvent(AuthenticationUIEvent.OnEmailResetChanged("resetTest@test.fr"))
            signInVM.formDataFlow.value.emailReset shouldBe "resetTest@test.fr"
            signInVM.formDataFlow.value.submitEmailReset shouldBe true
        }

        test("should set openResetDialog value to reverse value") {
            val signInVM = SignInViewModel(
                authFormValidationUseCase, resetPasswordUseCase,
                unifiedSignInUseCase, socialClients, dispatcher,
                FakeSignInErrorMessageProvider()
            )

            signInVM.openResetDialog.value shouldBe false
            signInVM.onEvent(AuthenticationUIEvent.OnForgotPasswordClicked)
            signInVM.openResetDialog.value shouldBe true
            signInVM.onEvent(AuthenticationUIEvent.HideForgotPasswordDialog)
            signInVM.openResetDialog.value shouldBe false
        }

        test("should reset password with new value") {
            runTest {
                val testDispatcher = UnconfinedTestDispatcher(testScheduler)
                Dispatchers.setMain(testDispatcher)

                val signInVM = SignInViewModel(
                    authFormValidationUseCase, resetPasswordUseCase,
                    unifiedSignInUseCase, socialClients, testDispatcher,
                    FakeSignInErrorMessageProvider()
                )

                every { validateEmailUseCase(any()) } returns
                    ValidationResult(true)

                every { resetPasswordUseCase(any()) } returns flowOf(
                    Result.Success(ResetPasswordSuccess)
                )

                try {
                    signInVM.onEvent(AuthenticationUIEvent.OnEmailResetChanged("test@test.fr"))
                    signInVM.formDataFlow.value.emailReset shouldBe "test@test.fr"

                    signInVM.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
                    signInVM.formDataFlow.value.emailReset shouldBe ""
                    signInVM.resetPasswordState.value shouldBe ResetPasswordEvent.Success
                } finally {
                    Dispatchers.resetMain()
                }
            }
        }
    }

    context("resetSignInState") {
        test("should reset signInState to Idle") {
            val emailPasswordParam = UnifiedSignInUseCase.Params.EmailPassword(
                "test@test.fr",
                "123456"
            )

            runTest {
                val testDispatcher = UnconfinedTestDispatcher(testScheduler)
                Dispatchers.setMain(testDispatcher)

                val signInVM = SignInViewModel(
                    authFormValidationUseCase, resetPasswordUseCase,
                    unifiedSignInUseCase, socialClients, testDispatcher,
                    FakeSignInErrorMessageProvider()
                )

                every { validateEmailUseCase(any()) } returns
                    ValidationResult(true)

                every { validatePasswordUseCase.validateLoginPassword(any()) } returns
                    ValidationResult(true)

                every { unifiedSignInUseCase.signIn(emailPasswordParam) } returns flowOf(
                    Result.Failure(SignInErrorHMM.WrongPassword)
                )

                try {
                    signInVM.signInState.value shouldBe SignInEvent.Idle

                    signInVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test@test.fr"))
                    signInVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("123456"))
                    advanceUntilIdle()

                    signInVM.onEvent(AuthenticationUIEvent.OnSignIn)
                    advanceUntilIdle()

                    signInVM.signInState.value shouldBe SignInEvent.Error("wrong password error")
                    signInVM.circularProgressLoading.value shouldBe false
                    signInVM.isSignIn shouldBe false

                    signInVM.resetSignInState()
                    signInVM.signInState.value shouldBe SignInEvent.Idle
                } finally {
                    Dispatchers.resetMain()
                }
            }
        }
    }

    context("resetPassword") {
        test("should emit Error when resetPasswordUseCase fails") {
            runTest {
                val testDispatcher = UnconfinedTestDispatcher(testScheduler)
                Dispatchers.setMain(testDispatcher)

                val signInVM = SignInViewModel(
                    authFormValidationUseCase, resetPasswordUseCase,
                    unifiedSignInUseCase, socialClients, testDispatcher,
                    FakeSignInErrorMessageProvider()
                )

                every { validateEmailUseCase("test@test.fr") } returns
                    ValidationResult(true)
                every { resetPasswordUseCase(any()) } returns flowOf(
                    Result.Failure(
                        ResetPasswordErrorHMM.Other
                    )
                )


                try {
                    signInVM.onEvent(
                        AuthenticationUIEvent.OnEmailResetChanged(
                            "test@test.fr"
                        )
                    )
                    signInVM.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
                    advanceUntilIdle()

                    signInVM.resetPasswordState.value shouldBe ResetPasswordEvent.Error(
                        "unknown error while resetting"
                    )
                } finally {
                    Dispatchers.resetMain()
                }
            }
        }

        test("should emit Loading when resetPasswordUseCase loads") {
            runTest {
                val testDispatcher = UnconfinedTestDispatcher(testScheduler)
                Dispatchers.setMain(testDispatcher)

                val signInVM = SignInViewModel(
                    authFormValidationUseCase, resetPasswordUseCase,
                    unifiedSignInUseCase, socialClients, testDispatcher,
                    FakeSignInErrorMessageProvider()
                )

                every { validateEmailUseCase("test@test.fr") } returns
                    ValidationResult(true)
                every { resetPasswordUseCase(any()) } returns flowOf(Result.Loading)

                try {
                    signInVM.onEvent(
                        AuthenticationUIEvent.OnEmailResetChanged(
                            "test@test.fr"
                        )
                    )
                    signInVM.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
                    advanceUntilIdle()

                    signInVM.resetPasswordState.value shouldBe ResetPasswordEvent.Loading
                } finally {
                    Dispatchers.resetMain()
                }
            }
        }

        test("should not call useCase if email reset is wrong") {
            runTest {
                val testDispatcher = UnconfinedTestDispatcher(testScheduler)
                Dispatchers.setMain(testDispatcher)

                val signInVM = SignInViewModel(
                    authFormValidationUseCase, resetPasswordUseCase,
                    unifiedSignInUseCase, socialClients, testDispatcher,
                    FakeSignInErrorMessageProvider()
                )

                every { validateEmailUseCase(any()) } returns ValidationResult()

                try {
                    signInVM.onEvent(
                        AuthenticationUIEvent.OnEmailResetChanged(
                            "test@test.fr"
                        )
                    )
                    signInVM.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
                    advanceUntilIdle()

                    verify(exactly = 0) { resetPasswordUseCase(any()) }
                    signInVM.resetPasswordState.value shouldBe ResetPasswordEvent.Idle
                } finally {
                    Dispatchers.resetMain()
                }
            }
        }
    }

    context("signInUnified") {
        test("should emit Success when signing in") {
            runTest {
                val testDispatcher = UnconfinedTestDispatcher(testScheduler)
                Dispatchers.setMain(testDispatcher)

                val signInVM = SignInViewModel(
                    authFormValidationUseCase, resetPasswordUseCase,
                    unifiedSignInUseCase, socialClients, testDispatcher,
                    FakeSignInErrorMessageProvider()
                )

                every { validateEmailUseCase(any()) } returns ValidationResult(true)
                every { validatePasswordUseCase.validateLoginPassword(any()) } returns
                    ValidationResult(true)

                every { unifiedSignInUseCase.signIn(any()) } returns flowOf(
                    Result.Success(
                        SignInSuccess
                    )
                )

                try {
                    signInVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test@test.fr"))
                    signInVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("123456"))
                    signInVM.onEvent(AuthenticationUIEvent.OnSignIn)
                    advanceUntilIdle()

                    signInVM.signInState.value shouldBe SignInEvent.Success
                    signInVM.circularProgressLoading.value shouldBe false
                    signInVM.isSignIn shouldBe false
                } finally {
                    Dispatchers.resetMain()
                }
            }
        }

        test("should emit Loading when waiting") {
            runTest {
                val testDispatcher = UnconfinedTestDispatcher(testScheduler)
                Dispatchers.setMain(testDispatcher)

                val signInVM = SignInViewModel(
                    authFormValidationUseCase, resetPasswordUseCase,
                    unifiedSignInUseCase, socialClients, testDispatcher,
                    FakeSignInErrorMessageProvider()
                )

                every { validateEmailUseCase(any()) } returns ValidationResult(true)
                every { validatePasswordUseCase.validateLoginPassword(any()) } returns
                    ValidationResult(true)
                every { unifiedSignInUseCase.signIn(any()) } returns flowOf(Result.Loading)

                try {
                    signInVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test@test.fr"))
                    signInVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("123456"))
                    signInVM.onEvent(AuthenticationUIEvent.OnSignIn)
                    advanceUntilIdle()

                    signInVM.circularProgressLoading.value shouldBe true
                    signInVM.signInState.value shouldBe SignInEvent.Loading
                } finally {
                    Dispatchers.resetMain()
                }
            }
        }
    }

    context("submit form") {
        test("should not be enabled when password is empty") {
            val signInVM = SignInViewModel(
                authFormValidationUseCase, resetPasswordUseCase,
                unifiedSignInUseCase, socialClients, dispatcher,
                FakeSignInErrorMessageProvider()
            )

            every { validateEmailUseCase("test@test.fr") } returns
                ValidationResult(true)
            every { validatePasswordUseCase.validateLoginPassword("") } returns
                ValidationResult()

            signInVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test@test.fr"))
            signInVM.onEvent(AuthenticationUIEvent.OnPasswordChanged(""))

            signInVM.formDataFlow.value.email shouldBe "test@test.fr"
            signInVM.formDataFlow.value.password shouldBe ""
            signInVM.formDataFlow.value.submit shouldBe false
        }

        test("should not be enabled when email is empty") {
            val signInVM = SignInViewModel(
                authFormValidationUseCase, resetPasswordUseCase,
                unifiedSignInUseCase, socialClients, dispatcher,
                FakeSignInErrorMessageProvider()
            )

            every { validateEmailUseCase(any()) } returns ValidationResult(false)
            every { validatePasswordUseCase.validateLoginPassword(any()) } returns
                ValidationResult(true)

            signInVM.onEvent(AuthenticationUIEvent.OnEmailChanged(""))
            signInVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("123456"))

            signInVM.formDataFlow.value.email shouldBe ""
            signInVM.formDataFlow.value.password shouldBe "123456"
            signInVM.formDataFlow.value.submit shouldBe false
        }

        test("should not be enabled when email fails") {
            val signInVM = SignInViewModel(
                authFormValidationUseCase, resetPasswordUseCase,
                unifiedSignInUseCase, socialClients, dispatcher,
                FakeSignInErrorMessageProvider()
            )

            every { validateEmailUseCase(any()) } returns ValidationResult()
            every { validatePasswordUseCase.validateLoginPassword(any()) } returns
                ValidationResult()

            signInVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test"))
            signInVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("123456"))

            signInVM.formDataFlow.value.email shouldBe "test"
            signInVM.formDataFlow.value.password shouldBe "123456"
            signInVM.formDataFlow.value.submit shouldBe false
        }
    }
})
