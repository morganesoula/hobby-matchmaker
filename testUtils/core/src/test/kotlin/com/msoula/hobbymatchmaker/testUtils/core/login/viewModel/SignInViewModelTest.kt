package com.msoula.hobbymatchmaker.testUtils.core.login.viewModel

import androidx.lifecycle.SavedStateHandle
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SocialMediaSignInUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider
import com.msoula.hobbymatchmaker.core.di.domain.useCase.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCase.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCase.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCase.ValidatePasswordUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.ResetPasswordEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.testUtils.core.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SignInViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val savedStateHandle = SavedStateHandle()
    private val authFormValidationUseCase = AuthFormValidationUseCase(
        ValidatePasswordUseCase(),
        ValidateEmailUseCase(),
        ValidateNameUseCase(),
        ValidateNameUseCase()
    )

    private val mockSignInUseCase = mockk<SignInUseCase>()
    private val mockResetPasswordUseCase = mockk<ResetPasswordUseCase>()
    private val mockStringResourcesProvider = mockk<StringResourcesProvider>()
    private val mockSocialMediaSignInUseCase = mockk<SocialMediaSignInUseCase>()

    @Test
    fun `assert new email input is saved in state`() = runTest(testDispatcher) {
        val testSignInViewModel = SignInViewModel(
            authFormValidationUseCase,
            savedStateHandle,
            mockSignInUseCase,
            mockResetPasswordUseCase,
            mockStringResourcesProvider,
            mockSocialMediaSignInUseCase
        )

        assert(testSignInViewModel.formDataFlow.value.email == "")
        testSignInViewModel.onEvent(AuthenticationUIEvent.OnEmailChanged("testEmail"))
        assert(testSignInViewModel.formDataFlow.value.email == "testEmail")
    }

    @Test
    fun `assert new password input is saved in state`() = runTest(testDispatcher) {
        val testSignInViewModel = SignInViewModel(
            authFormValidationUseCase,
            savedStateHandle,
            mockSignInUseCase,
            mockResetPasswordUseCase,
            mockStringResourcesProvider,
            mockSocialMediaSignInUseCase
        )

        assert(testSignInViewModel.formDataFlow.value.password == "")
        testSignInViewModel.onEvent(
            AuthenticationUIEvent.OnPasswordChanged("testPassword")
        )
        assert(testSignInViewModel.formDataFlow.value.password == "testPassword")
    }

    @Test
    fun `assert update password dialog is visible when event is triggered`() =
        runTest(testDispatcher) {
            val testSignInViewModel = SignInViewModel(
                authFormValidationUseCase,
                savedStateHandle,
                mockSignInUseCase,
                mockResetPasswordUseCase,
                mockStringResourcesProvider,
                mockSocialMediaSignInUseCase
            )

            assert(!testSignInViewModel.openResetDialog.value)
            testSignInViewModel.onEvent(AuthenticationUIEvent.OnForgotPasswordClicked)
            assert(testSignInViewModel.openResetDialog.value)
            testSignInViewModel.onEvent(AuthenticationUIEvent.HideForgotPasswordDialog)
            assert(!testSignInViewModel.openResetDialog.value)
        }

    @Test
    fun `assert email reset input is saved in state`() = runTest(testDispatcher) {
        val testSignInViewModel = SignInViewModel(
            authFormValidationUseCase,
            savedStateHandle,
            mockSignInUseCase,
            mockResetPasswordUseCase,
            mockStringResourcesProvider,
            mockSocialMediaSignInUseCase
        )

        assert(testSignInViewModel.formDataFlow.value.emailReset == "")
        testSignInViewModel.onEvent(
            AuthenticationUIEvent.OnEmailResetChanged("testEmailReset")
        )
        assert(testSignInViewModel.formDataFlow.value.emailReset == "testEmailReset")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `assert password reset email is sent when resetting password`() = runTest(testDispatcher) {
        val testSignInViewModel = SignInViewModel(
            authFormValidationUseCase,
            savedStateHandle,
            mockSignInUseCase,
            mockResetPasswordUseCase,
            mockStringResourcesProvider,
            mockSocialMediaSignInUseCase
        )

        every { mockResetPasswordUseCase(any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(ResetPasswordSuccess))
        }

        testSignInViewModel.onEvent(
            AuthenticationUIEvent.OnEmailResetChanged("testEmailReset")
        )
        assert(testSignInViewModel.formDataFlow.value.emailReset == "testEmailReset")
        testSignInViewModel.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
        advanceUntilIdle()

        assert(testSignInViewModel.formDataFlow.value.emailReset == "")
        assert(testSignInViewModel.resetPasswordState.value == ResetPasswordEvent.Success)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `assert password reset email is not sent when resetting password`() =
        runTest(testDispatcher) {
            val testSignInViewModel = SignInViewModel(
                authFormValidationUseCase,
                savedStateHandle,
                mockSignInUseCase,
                mockResetPasswordUseCase,
                mockStringResourcesProvider,
                mockSocialMediaSignInUseCase
            )

            every { mockResetPasswordUseCase(any()) } returns flow {
                emit(Result.Loading)
                emit(Result.Failure(RandomError("random error when resetting password")))
            }

            testSignInViewModel.onEvent(
                AuthenticationUIEvent.OnEmailResetChanged("testEmailReset")
            )
            assert(testSignInViewModel.formDataFlow.value.emailReset == "testEmailReset")
            testSignInViewModel.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
            advanceUntilIdle()

            assert(testSignInViewModel.formDataFlow.value.emailReset == "testEmailReset")
            assert(
                testSignInViewModel.resetPasswordState.value ==
                    ResetPasswordEvent.Error("random error when resetting password")
            )
        }

    private class RandomError(override val message: String) : AppError

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `assert signIn fails when signInUseCase fails`() = runTest(testDispatcher) {
        val testSignInViewModel = SignInViewModel(
            authFormValidationUseCase,
            savedStateHandle,
            mockSignInUseCase,
            mockResetPasswordUseCase,
            mockStringResourcesProvider,
            mockSocialMediaSignInUseCase
        )

        testSignInViewModel.circularProgressLoading.value = true

        every { mockSignInUseCase(any()) } returns flow {
            emit(Result.Failure(SignInError("error while signing in")))
        }

        testSignInViewModel.onEvent(AuthenticationUIEvent.OnSignIn)
        advanceUntilIdle()
        assert(!testSignInViewModel.circularProgressLoading.value)
        assert(testSignInViewModel.signInState.value == SignInEvent.Error("error while signing in"))
    }

    private class SignInError(override val message: String) : AppError

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `assert signIn succeeds when signInUseCase succeeds`() = runTest(testDispatcher) {
        val testSignInViewModel = SignInViewModel(
            authFormValidationUseCase,
            savedStateHandle,
            mockSignInUseCase,
            mockResetPasswordUseCase,
            mockStringResourcesProvider,
            mockSocialMediaSignInUseCase
        )

        testSignInViewModel.circularProgressLoading.value = true

        every { mockSignInUseCase(any()) } returns flow {
            emit(Result.Success(SignInSuccess))
        }

        testSignInViewModel.onEvent(AuthenticationUIEvent.OnSignIn)
        advanceUntilIdle()
        assert(!testSignInViewModel.circularProgressLoading.value)
        assert(testSignInViewModel.signInState.value == SignInEvent.Success)
    }

}
