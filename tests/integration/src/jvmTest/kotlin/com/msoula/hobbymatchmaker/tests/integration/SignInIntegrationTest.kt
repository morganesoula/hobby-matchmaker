package com.msoula.hobbymatchmaker.tests.integration

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthFirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.UIText
import com.msoula.hobbymatchmaker.core.di.domain.useCases.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.tests.fakes.FakeAuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.tests.fakes.FakeSessionLocalDataSource
import com.msoula.hobbymatchmaker.tests.fakes.FakeSessionRemoteDataSource
import com.msoula.hobbymatchmaker.tests.modules.signInTestModule
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.mp.KoinPlatform.getKoin
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidatePasswordUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthUiEventModel
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.ResetPasswordEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import dev.gitlive.firebase.auth.AuthCredential
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent

@OptIn(ExperimentalCoroutinesApi::class)
class SignInIntegrationTest : FunSpec({

    val dispatcher = StandardTestDispatcher()
    val testScope = TestScope(dispatcher)

    beforeSpec {
        startKoin { modules(signInTestModule) }
    }

    afterSpec {
        stopKoin()
    }

    beforeTest {
        val fakeAuth: FakeAuthenticationRemoteDataSource = getKoin().get()
        val fakeLocal: FakeSessionLocalDataSource = getKoin().get()
        val fakeRemote: FakeSessionRemoteDataSource = getKoin().get()

        fakeRemote.created.clear()
        fakeRemote.createUserResult = AppResult.Success(Unit)
        fakeLocal.setIsConnected(false)
        fakeLocal.setShouldShowGuestDialog(true)

        fakeAuth.createUserResult = AppResult.Success("uid-123")
        fakeAuth.signUpDelayMs = 0L

        fakeAuth.signInResult = AppResult.Success("uid-123")
        fakeAuth.signInDelayMs = 0L

        fakeAuth.resetResult = AppResult.Success(Unit)
        fakeAuth.resetDelayMs = 0L

        fakeAuth.socialResult = AppResult.Success(
            AuthFirebaseUser(
                uid = "uid-123",
                email = "user@mail.com",
                providers = listOf("google.com")
            )
        )
        fakeAuth.socialDelayMs = 0L
    }

    test("Email/Password happy path -> OnSignInSuccess, Idle, isConnected=true") {
        runTest(dispatcher) {
            val vm = SignInViewModel(
                authFormValidationUseCases = AuthFormValidationUseCase(
                    validatePasswordUseCase = ValidatePasswordUseCase(),
                    validateEmailUseCase = ValidateEmailUseCase(),
                    validateFirstNameUseCase = ValidateNameUseCase(),
                    validateLastNameUseCase = ValidateNameUseCase()
                ),
                resetPasswordUseCase = getKoin().get(),
                setShouldShowGuestDialogUseCase = getKoin().get(),
                observeShouldShowGuestDialog = getKoin().get(),
                unifiedSignInUseCase = getKoin().get(),
                socialClients = emptyMap(),
                defaultErrorMessageMapper = getKoin().get(),
                externalScope = testScope
            )

            val fakeAuth: FakeAuthenticationRemoteDataSource = getKoin().get()
            val fakeLocal: FakeSessionLocalDataSource = getKoin().get()
            fakeAuth.signInDelayMs = 1_000L

            vm.onEvent(AuthenticationUIEvent.OnEmailChanged("user@mail.com"))
            vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("secret"))
            vm.formDataFlow.value.submit shouldBe true

            vm.oneTimeEventChannelFlow.test {
                vm.onEvent(AuthenticationUIEvent.OnSignIn)

                runCurrent()
                vm.signInState.value shouldBe SignInEvent.Loading

                advanceTimeBy(1_000L)
                advanceUntilIdle()

                awaitItem() shouldBe AuthUiEventModel.OnSignInSuccess
                vm.signInState.value shouldBe SignInEvent.Idle

                (fakeLocal.observeIsConnected() as MutableStateFlow<Boolean>).value shouldBe true

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("Email/Password failure -> ShowError(mapped), Idle, isConnected reste false") {
        runTest(dispatcher) {
            val vm = SignInViewModel(
                authFormValidationUseCases = AuthFormValidationUseCase(
                    validatePasswordUseCase = ValidatePasswordUseCase(),
                    validateEmailUseCase = ValidateEmailUseCase(),
                    validateFirstNameUseCase = ValidateNameUseCase(),
                    validateLastNameUseCase = ValidateNameUseCase()
                ),
                resetPasswordUseCase = getKoin().get(),
                setShouldShowGuestDialogUseCase = getKoin().get(),
                observeShouldShowGuestDialog = getKoin().get(),
                unifiedSignInUseCase = getKoin().get(),
                socialClients = emptyMap(),
                defaultErrorMessageMapper = getKoin().get(),
                externalScope = testScope
            )

            val fakeAuth: FakeAuthenticationRemoteDataSource = getKoin().get()
            val fakeLocal: FakeSessionLocalDataSource = getKoin().get()

            fakeAuth.signInResult = AppResult.Failure(AppError.Domain.Unauthorized)
            fakeAuth.signInDelayMs = 1_000L

            vm.onEvent(AuthenticationUIEvent.OnEmailChanged("user@mail.com"))
            vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("secret"))
            vm.formDataFlow.value.submit shouldBe true

            vm.oneTimeEventChannelFlow.test {
                vm.onEvent(AuthenticationUIEvent.OnSignIn)

                runCurrent()
                vm.signInState.value shouldBe SignInEvent.Loading

                advanceTimeBy(1_000L)
                advanceUntilIdle()

                val event = awaitItem()
                (event as AuthUiEventModel.ShowError).error shouldBe UIText.Plain("mapped")
                vm.signInState.value shouldBe SignInEvent.Idle

                (fakeLocal.observeIsConnected() as MutableStateFlow<Boolean>).value shouldBe false

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("Reset password success -> OnResetPasswordSuccess, Idle") {
        runTest(dispatcher) {
            val vm = SignInViewModel(
                authFormValidationUseCases = AuthFormValidationUseCase(
                    validatePasswordUseCase = ValidatePasswordUseCase(),
                    validateEmailUseCase = ValidateEmailUseCase(),
                    validateFirstNameUseCase = ValidateNameUseCase(),
                    validateLastNameUseCase = ValidateNameUseCase()
                ),
                resetPasswordUseCase = getKoin().get(),
                setShouldShowGuestDialogUseCase = getKoin().get(),
                observeShouldShowGuestDialog = getKoin().get(),
                unifiedSignInUseCase = getKoin().get(),
                socialClients = emptyMap(),
                defaultErrorMessageMapper = getKoin().get(),
                externalScope = testScope
            )

            val fakeAuth: FakeAuthenticationRemoteDataSource = getKoin().get()
            fakeAuth.resetResult = AppResult.Success(Unit)
            fakeAuth.resetDelayMs = 500L

            vm.onEvent(AuthenticationUIEvent.OnEmailResetChanged("reset@mail.com"))
            vm.formDataFlow.value.submitEmailReset shouldBe true

            vm.oneTimeEventChannelFlow.test {
                vm.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)

                runCurrent()
                vm.resetPasswordState.value shouldBe ResetPasswordEvent.Loading

                advanceTimeBy(500L)
                advanceUntilIdle()

                awaitItem() shouldBe AuthUiEventModel.OnResetPasswordSuccess
                vm.resetPasswordState.value shouldBe ResetPasswordEvent.Idle

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("Social sign-in (Google) happy path -> OnSignInSuccess, Idle, isConnected=true") {
        runTest(dispatcher) {
            class FakeSocialClient(private val credential: AuthCredential?) : SocialUIClient {
                override val providerType = ProviderType.GOOGLE
                override suspend fun getCredential(): AuthCredential? = credential
            }

            val socialCredential = mockk<AuthCredential>(relaxed = true)
            val socialClients = mapOf(ProviderType.GOOGLE to FakeSocialClient(socialCredential))

            val vm = SignInViewModel(
                authFormValidationUseCases = AuthFormValidationUseCase(
                    validatePasswordUseCase = ValidatePasswordUseCase(),
                    validateEmailUseCase = ValidateEmailUseCase(),
                    validateFirstNameUseCase = ValidateNameUseCase(),
                    validateLastNameUseCase = ValidateNameUseCase()
                ),
                resetPasswordUseCase = getKoin().get(),
                setShouldShowGuestDialogUseCase = getKoin().get(),
                observeShouldShowGuestDialog = getKoin().get(),
                unifiedSignInUseCase = getKoin().get(),
                socialClients = socialClients,
                defaultErrorMessageMapper = getKoin().get(),
                externalScope = testScope
            )

            val fakeAuth: FakeAuthenticationRemoteDataSource = getKoin().get()
            val fakeLocal: FakeSessionLocalDataSource = getKoin().get()

            fakeAuth.socialDelayMs = 1_000L

            vm.oneTimeEventChannelFlow.test {
                vm.onEvent(AuthenticationUIEvent.OnGoogleButtonClicked)

                runCurrent()
                vm.signInState.value shouldBe SignInEvent.Loading

                advanceTimeBy(1_000L)
                advanceUntilIdle()

                awaitItem() shouldBe AuthUiEventModel.OnSignInSuccess
                vm.signInState.value shouldBe SignInEvent.Idle

                (fakeLocal.observeIsConnected() as MutableStateFlow<Boolean>).value shouldBe true

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("Social sign-in without credential -> ShowError('Unable to get credentials'), no Loading") {
        runTest(dispatcher) {
            class NullSocialClient : SocialUIClient {
                override val providerType = ProviderType.FACEBOOK
                override suspend fun getCredential(): AuthCredential? = null
            }

            val vm = SignInViewModel(
                authFormValidationUseCases = AuthFormValidationUseCase(
                    ValidatePasswordUseCase(), ValidateEmailUseCase(),
                    ValidateNameUseCase(), ValidateNameUseCase()
                ),
                resetPasswordUseCase = getKoin().get(),
                setShouldShowGuestDialogUseCase = getKoin().get(),
                observeShouldShowGuestDialog = getKoin().get(),
                unifiedSignInUseCase = getKoin().get(),
                socialClients = mapOf(ProviderType.FACEBOOK to NullSocialClient()),
                defaultErrorMessageMapper = getKoin().get(),
                externalScope = testScope
            )

            vm.oneTimeEventChannelFlow.test {
                vm.onEvent(AuthenticationUIEvent.OnAppleButtonClicked)

                val event = awaitItem()
                (event as AuthUiEventModel.ShowError).error shouldBe UIText.Plain("Unable to get credentials")
                vm.signInState.value shouldBe SignInEvent.Idle

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

})
