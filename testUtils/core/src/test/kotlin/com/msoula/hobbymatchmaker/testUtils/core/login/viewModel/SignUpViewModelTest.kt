package com.msoula.hobbymatchmaker.testUtils.core.login.viewModel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpErrors
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidatePasswordUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.models.SignUpStateModel
import com.msoula.hobbymatchmaker.testUtils.core.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SignUpViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val loginValidateFormUseCase = LoginValidateFormUseCase(
        validateEmail = ValidateEmailUseCase(),
        validatePassword = ValidatePasswordUseCase(),
        validateFirstName = ValidateNameUseCase()
    )

    private val savedStateHandle = SavedStateHandle()

    private val mockStringResourcesProvider = mockk<StringResourcesProvider>()
    private val mockSignUpUseCase = mockk<SignUpUseCase>()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun `assert new email input is saved in SavedStateHandle`() = runTest(testDispatcher) {
        val signUpViewModel = SignUpViewModel(
            loginValidateFormUseCase = loginValidateFormUseCase,
            mockStringResourcesProvider,
            savedStateHandle,
            mockSignUpUseCase
        )

        signUpViewModel.onEvent(AuthenticationUIEvent.OnEmailChanged("testEmail"))

        val signUpState = savedStateHandle.get<SignUpStateModel>("signUpState")
        assert(signUpState != null)
        assertEquals("testEmail", signUpState?.email)
    }

    @Test
    fun `assert email input is trimmed when saved in SavedStateHandle`() = runTest(testDispatcher) {
        val signUpViewModel = SignUpViewModel(
            loginValidateFormUseCase = loginValidateFormUseCase,
            mockStringResourcesProvider,
            savedStateHandle,
            mockSignUpUseCase
        )

        signUpViewModel.onEvent(AuthenticationUIEvent.OnEmailChanged("testEmail "))

        val signUpState = savedStateHandle.get<SignUpStateModel>("signUpState")
        assert(signUpState != null)
        assertFalse("testEmail " == signUpState?.email)
        assertEquals("testEmail", signUpState?.email)
    }

    @Test
    fun `assert new password input is saved in SavedStateHandle`() = runTest(testDispatcher) {
        val signUpViewModel = SignUpViewModel(
            loginValidateFormUseCase = loginValidateFormUseCase,
            mockStringResourcesProvider,
            savedStateHandle,
            mockSignUpUseCase
        )

        signUpViewModel.onEvent(AuthenticationUIEvent.OnPasswordChanged("testMotDePasse"))

        val signUpState = savedStateHandle.get<SignUpStateModel>("signUpState")
        assert(signUpState != null)
        assertEquals("testMotDePasse", signUpState?.password)
    }

    @Test
    fun `assert new name input is saved in SavedStateHandle`() = runTest(testDispatcher) {
        val signUpViewModel = SignUpViewModel(
            loginValidateFormUseCase = loginValidateFormUseCase,
            mockStringResourcesProvider,
            savedStateHandle,
            mockSignUpUseCase
        )

        signUpViewModel.onEvent(AuthenticationUIEvent.OnFirstNameChanged("testName"))

        val signUpState = savedStateHandle.get<SignUpStateModel>("signUpState")
        assert(signUpState != null)
        assertEquals("testName", signUpState?.firstName)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `assert Error event is sent when sign up fails`() = runTest(testDispatcher) {
        val signUpViewModel = SignUpViewModel(
            loginValidateFormUseCase = loginValidateFormUseCase,
            mockStringResourcesProvider,
            savedStateHandle,
            mockSignUpUseCase
        )

        coEvery { mockSignUpUseCase(any()) } returns flow { emit(Result.Failure(SignUpErrors.UserDisabled)) }
        coEvery { mockStringResourcesProvider.getString(2132017350) } returns "User disabled"

        signUpViewModel.onEvent(AuthenticationUIEvent.OnSignUp)
        advanceUntilIdle()

        signUpViewModel.signUpState.test {
            assert(awaitItem() == SignUpEvent.Error("User disabled"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `assert Success event is sent when sign up succeeds`() = runTest(testDispatcher) {
        val signUpViewModel = SignUpViewModel(
            loginValidateFormUseCase = loginValidateFormUseCase,
            mockStringResourcesProvider,
            savedStateHandle,
            mockSignUpUseCase
        )

        coEvery { mockSignUpUseCase(any()) } returns flow { emit(Result.Success(SignUpSuccess("randomUid"))) }

        signUpViewModel.onEvent(AuthenticationUIEvent.OnSignUp)
        advanceUntilIdle()

        signUpViewModel.signUpState.test {
            assert(awaitItem() == SignUpEvent.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
