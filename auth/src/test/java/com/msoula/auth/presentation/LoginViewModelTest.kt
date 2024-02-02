package com.msoula.auth.presentation

import android.text.TextUtils
import android.util.Log
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.msoula.auth.data.AuthUIEvent
import com.msoula.auth.data.FakeAuthRepositoryImpl
import com.msoula.auth.data.FakeStringResourcesProvider
import com.msoula.di.domain.use_case.AuthFormValidationUseCase
import com.msoula.di.domain.use_case.ValidateEmail
import com.msoula.di.domain.use_case.ValidateName
import com.msoula.di.domain.use_case.ValidatePassword
import com.msoula.di.navigation.NavigatorImpl
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

import com.msoula.auth.R.string as StringRes

class LoginViewModelTest {

    private lateinit var loginViewModel: LoginViewModel
    private val resourceProvider = FakeStringResourcesProvider()
    private val navigator = NavigatorImpl()

    @Before
    fun setUp() {
        mockkStatic(TextUtils::class)
        mockkStatic(Log::class)

        loginViewModel = LoginViewModel(
            authFormValidationUseCases = AuthFormValidationUseCase(
                ValidateEmail(),
                ValidatePassword(),
                ValidateName(),
                ValidateName()
            ),
            authRepository = FakeAuthRepositoryImpl(),
            resourceProvider = resourceProvider,
            ioDispatcher = Dispatchers.IO,
            navigator = navigator
        )
    }

    @Test
    fun getLogInState() {
        val initialLoginState = loginViewModel.logInState.value

        assertThat(initialLoginState.email).isEmpty()
        assertThat(initialLoginState.logInError).isNull()
        assertThat(initialLoginState.submitEmailReset).isFalse()
        assertThat(initialLoginState.submit).isFalse()

        loginViewModel.onEvent(AuthUIEvent.OnEmailChanged("test@test.com"))
        loginViewModel.onEvent(AuthUIEvent.OnPasswordChanged("testPassword"))

        val updatedLoginState = loginViewModel.logInState.value

        assertThat(updatedLoginState.email).isEqualTo("test@test.com")
        assertThat(updatedLoginState.password).isEqualTo("testPassword")
        assertThat(updatedLoginState.submit).isTrue()
    }

    @Test
    fun getOpenResetDialog() {
        val openDialog = loginViewModel.openResetDialog.value

        assertThat(openDialog).isFalse()

        loginViewModel.onEvent(AuthUIEvent.OnForgotPasswordClicked)

        var updatedOpenDialog = loginViewModel.openResetDialog.value
        assertThat(updatedOpenDialog).isTrue()

        loginViewModel.onEvent(AuthUIEvent.HideForgotPasswordDialog)
        updatedOpenDialog = loginViewModel.openResetDialog.value
        assertThat(updatedOpenDialog).isFalse()
    }

    @Test
    fun getResettingEmailSent() = runTest {
        loginViewModel.onEvent(AuthUIEvent.OnEmailResetChanged("soula.morgane35@gmail.com"))
        val resetEmail = loginViewModel.resettingEmailSent.value
        assertThat(resetEmail).isFalse()

        loginViewModel.onEvent(AuthUIEvent.OnResetPasswordConfirmed)
        delay(2000)

        val updatedResetEmail = loginViewModel.resettingEmailSent.value
        assertThat(updatedResetEmail).isTrue()
    }

    @Test
    fun onEvent(): Unit = runBlocking {
        every { TextUtils.isEmpty(any()) } returns false
        every { Log.e("HMM", "Invalid credentials") } returns 1

        loginViewModel.onEvent(AuthUIEvent.OnEmailChanged("test@test.com"))
        var loginState = loginViewModel.logInState.value
        assertThat(loginState.submit).isFalse()

        loginViewModel.onEvent(AuthUIEvent.OnPasswordChanged("pass"))
        loginState = loginViewModel.logInState.value
        assertThat(loginState.submit).isTrue()

        assertThat(loginViewModel.circularProgressLoading.value).isFalse()
        loginViewModel.onEvent(AuthUIEvent.OnLogIn)
        assertThat(loginViewModel.circularProgressLoading.value).isTrue()

        delay(2000)
        loginState = loginViewModel.logInState.value

        assertThat(loginViewModel.circularProgressLoading.value).isFalse()
        assertThat(loginState.logInError).isNotNull()
        assertThat(loginState.logInError).isEqualTo(resourceProvider.getString(StringRes.login_error))

        loginViewModel.onEvent(AuthUIEvent.OnPasswordChanged("password"))
        loginViewModel.onEvent(AuthUIEvent.OnLogIn)
        assertThat(loginViewModel.circularProgressLoading.value).isTrue()

        delay(2000)
        assertThat(loginViewModel.circularProgressLoading.value).isFalse()
    }
}