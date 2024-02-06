package com.msoula.auth.presentation

import android.text.TextUtils
import android.util.Log
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain

import org.junit.Before
import org.junit.Test

import com.msoula.auth.R.string as StringRes

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    private lateinit var signUpViewModel: SignUpViewModel
    private val resourcesProvider = FakeStringResourcesProvider()

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        mockkStatic(TextUtils::class)
        mockkStatic(Log::class)

        signUpViewModel = SignUpViewModel(
            authRepository = FakeAuthRepositoryImpl(),
            authFormValidationUseCases = AuthFormValidationUseCase(
                ValidateEmail(),
                ValidatePassword(),
                ValidateName(),
                ValidateName()
            ),
            resourceProvider = resourcesProvider,
            ioDispatcher = Dispatchers.IO,
            navigator = NavigatorImpl()
        )
    }

    @Test
    fun getRegistrationState() {
        val initialState = signUpViewModel.registrationFormState.value

        assertThat(initialState.firstName).isEmpty()
        assertThat(initialState.lastName).isEmpty()
        assertThat(initialState.email).isEmpty()
        assertThat(initialState.submit).isFalse()

        signUpViewModel.onEvent(AuthUIEvent.OnEmailChanged("testSignUp@test.com"))
        signUpViewModel.onEvent(AuthUIEvent.OnFirstNameChanged("firstName"))
        signUpViewModel.onEvent(AuthUIEvent.OnLastNameChanged("lastName"))
        signUpViewModel.onEvent(AuthUIEvent.OnPasswordChanged("Password123!"))

        val updatedInitialState = signUpViewModel.registrationFormState.value

        assertThat(updatedInitialState.firstName).isEqualTo("firstName")
        assertThat(updatedInitialState.lastName).isEqualTo("lastName")
        assertThat(updatedInitialState.email).isEqualTo("testSignUp@test.com")
        assertThat(updatedInitialState.submit).isTrue()
    }

    @Test
    fun onEvent(): Unit = runBlocking {
        every { TextUtils.isEmpty(any()) } returns false
        every { Log.e("HMM", "Email already associated") } returns 0

        signUpViewModel.onEvent(AuthUIEvent.OnEmailChanged("testSignUp@test.com"))
        signUpViewModel.onEvent(AuthUIEvent.OnFirstNameChanged("firstName"))
        signUpViewModel.onEvent(AuthUIEvent.OnLastNameChanged("lastName"))
        signUpViewModel.onEvent(AuthUIEvent.OnPasswordChanged("Password123!"))

        signUpViewModel.onEvent(AuthUIEvent.OnSignUp)

        assertThat(signUpViewModel.signUpCircularProgress.value).isFalse()
        delay(2000)

        val initialState = signUpViewModel.registrationFormState.value
        assertThat(initialState.signUpError).isNotNull()
        assertThat(initialState.signUpError).isEqualTo(resourcesProvider.getString(StringRes.signup_error))

        signUpViewModel.onEvent(AuthUIEvent.OnPasswordChanged("Password123?"))
        signUpViewModel.onEvent(AuthUIEvent.OnSignUp)
        assertThat(signUpViewModel.signUpCircularProgress.value).isFalse()
    }
}