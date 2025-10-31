package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.UnifiedSignInUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.UIText
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthUiEventModel
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.ResetPasswordEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveShouldShowGuestDialogUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetShouldShowGuestDialogUseCase
import dev.gitlive.firebase.auth.AuthCredential
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest : FunSpec({
    val scheduler = TestCoroutineScheduler()
    val dispatcher = StandardTestDispatcher(scheduler)
    val testScope = TestScope(dispatcher + Job())

    lateinit var authValidationUC: AuthFormValidationUseCase
    val emailValidator = ValidateEmailUseCase()
    val pwdValidator = ValidatePasswordUseCase()
    lateinit var resetPasswordUC: ResetPasswordUseCase
    lateinit var setGuestDialogUC: SetShouldShowGuestDialogUseCase
    lateinit var observeGuestDialogUC: ObserveShouldShowGuestDialogUseCase
    lateinit var unifiedSignInUC: UnifiedSignInUseCase
    lateinit var googleClient: SocialUIClient
    lateinit var appleClient: SocialUIClient
    lateinit var facebookClient: SocialUIClient
    lateinit var socialClients: Map<ProviderType, SocialUIClient>
    lateinit var guestFlagFlow: MutableStateFlow<Boolean>

    val errorMapper = object : ErrorMessageMapper {
        override fun toUIText(error: AppError): UIText {
            return UIText.Plain("err")
        }
    }

    fun pump() = scheduler.runCurrent()
    fun elapse(ms: Long) {
        scheduler.advanceTimeBy(ms)
        scheduler.runCurrent()
    }

    fun buildVM(): SignInViewModel {
        authValidationUC = mockk(relaxed = true)
        resetPasswordUC = mockk(relaxed = true)
        setGuestDialogUC = mockk(relaxed = true)

        guestFlagFlow = MutableStateFlow(true)
        observeGuestDialogUC = mockk {
            every { this@mockk() } returns guestFlagFlow
        }

        unifiedSignInUC = mockk(relaxed = true)

        googleClient = mockk(relaxed = true)
        appleClient = mockk(relaxed = true)
        facebookClient = mockk(relaxed = true)
        socialClients = mapOf(
            ProviderType.GOOGLE to googleClient,
            ProviderType.APPLE to appleClient,
            ProviderType.FACEBOOK to facebookClient
        )

        return SignInViewModel(
            authFormValidationUseCases = authValidationUC,
            resetPasswordUseCase = resetPasswordUC,
            setShouldShowGuestDialogUseCase = setGuestDialogUC,
            observeShouldShowGuestDialog = observeGuestDialogUC,
            unifiedSignInUseCase = unifiedSignInUC,
            socialClients = socialClients,
            defaultErrorMessageMapper = errorMapper,
            externalScope = testScope
        )
    }

    beforeSpec { Dispatchers.setMain(dispatcher) }
    afterSpec { Dispatchers.resetMain(); unmockkAll() }
    beforeTest { clearAllMocks() }

    test("initial states: signIn=Idle, reset=Idle, shouldShowGuestDialog=true") {
        val vm = buildVM()
        vm.signInState.value shouldBe SignInEvent.Idle
        vm.resetPasswordState.value shouldBe ResetPasswordEvent.Idle
        vm.shouldShowGuestDialog.value shouldBe true
    }

    test("validateInput sets submit=true when email & password valid") {
        val vm = buildVM()

        every { authValidationUC.validateEmailUseCase("mail@example.com") } returns ValidationResult(
            true,
            null
        )

        every { authValidationUC.validatePasswordUseCase.validateLoginPassword("Secret123") } returns ValidationResult(
            true,
            null
        )

        vm.onEvent(AuthenticationUIEvent.OnEmailChanged("mail@example.com  "))
        vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("Secret123  "))
        pump()

        vm.formDataFlow.value.submit.shouldBeTrue()
        vm.formDataFlow.value.email shouldBe "mail@example.com"
        vm.formDataFlow.value.password shouldBe "Secret123"
    }

    test("validateInput sets submit=false when password invalid") {
        val vm = buildVM()

        vm.onEvent(AuthenticationUIEvent.OnEmailChanged("mail@example.com"))
        vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("bad"))
        pump()

        vm.formDataFlow.value.submit.shouldBeFalse()
    }

    test("OnSignIn success -> emits OnSignInSuccess, state back to Idle; args are trimEnd-ed") {
        val vm = buildVM()

        coEvery { unifiedSignInUC.invoke(any()) } coAnswers {
            delay(1)
            AppResult.Success(SignInSuccess)
        }

        vm.onEvent(AuthenticationUIEvent.OnEmailChanged("mail@example.com  "))
        vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("Secret123  "))
        pump()

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(AuthenticationUIEvent.OnSignIn)
            pump()

            vm.signInState.value shouldBe SignInEvent.Loading
            scheduler.advanceTimeBy(1)
            pump()

            awaitItem() shouldBe AuthUiEventModel.OnSignInSuccess
            vm.signInState.value shouldBe SignInEvent.Idle

            coVerify(exactly = 1) {
                unifiedSignInUC.invoke(
                    UnifiedSignInUseCase.Params.EmailPassword("mail@example.com", "Secret123")
                )
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("OnSignIn failure -> emits ShowError(err), state back to Idle") {
        val vm = buildVM()

        coEvery { unifiedSignInUC.invoke(any()) } coAnswers {
            delay(1)
            AppResult.Failure(AppError.Domain.Forbidden)
        }

        vm.onEvent(AuthenticationUIEvent.OnEmailChanged("mail@example.com"))
        vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("Secret123"))
        pump()

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(AuthenticationUIEvent.OnSignIn)
            pump()
            vm.signInState.value shouldBe SignInEvent.Loading

            scheduler.advanceTimeBy(1); pump()

            val ev = awaitItem() as AuthUiEventModel.ShowError
            (ev.error as UIText.Plain).value shouldBe "err"
            vm.signInState.value shouldBe SignInEvent.Idle
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("Forgot password dialog toggles openResetDialog") {
        val vm = buildVM()
        vm.openResetDialog.value shouldBe false

        vm.onEvent(AuthenticationUIEvent.OnForgotPasswordClicked)
        vm.openResetDialog.value shouldBe true

        vm.onEvent(AuthenticationUIEvent.HideForgotPasswordDialog)
        vm.openResetDialog.value shouldBe false
    }

    test("ResetPassword: ignored when submitEmailReset=false") {
        val vm = buildVM()
        every { authValidationUC.validateEmailUseCase("bad") } returns ValidationResult(
            false,
            1
        )

        vm.onEvent(AuthenticationUIEvent.OnEmailResetChanged("bad"))
        pump()

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
            pump()
            vm.resetPasswordState.value shouldBe ResetPasswordEvent.Idle
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { resetPasswordUC.invoke(any()) }
    }

    test("ResetPassword success -> Loading then Success event + clears emailReset + Idle") {
        val vm = buildVM()
        every { authValidationUC.validateEmailUseCase("mail@example.com") } returns ValidationResult(
            true,
            null
        )
        coEvery { resetPasswordUC.invoke(any()) } coAnswers {
            delay(1)
            AppResult.Success(Unit)
        }

        vm.onEvent(AuthenticationUIEvent.OnEmailResetChanged("mail@example.com"))
        pump()

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
            pump()
            vm.resetPasswordState.value shouldBe ResetPasswordEvent.Loading

            scheduler.advanceTimeBy(1); pump()

            awaitItem() shouldBe AuthUiEventModel.OnResetPasswordSuccess
            vm.resetPasswordState.value shouldBe ResetPasswordEvent.Idle
            vm.formDataFlow.value.emailReset shouldBe ""
            cancelAndIgnoreRemainingEvents()
        }

        coVerify {
            resetPasswordUC.invoke(Parameters.StringParam("mail@example.com"))
        }
    }

    test("ResetPassword failure -> Loading then ShowError(err) + Idle") {
        val vm = buildVM()
        every { authValidationUC.validateEmailUseCase("mail@example.com") } returns ValidationResult(
            true,
            null
        )
        coEvery { resetPasswordUC.invoke(any()) } coAnswers {
            delay(1)
            AppResult.Failure(AppError.Domain.NotFound)
        }

        vm.onEvent(AuthenticationUIEvent.OnEmailResetChanged("mail@example.com"))
        pump()

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
            pump()
            vm.resetPasswordState.value shouldBe ResetPasswordEvent.Loading

            scheduler.advanceTimeBy(1); pump()

            val ev = awaitItem() as AuthUiEventModel.ShowError
            (ev.error as UIText.Plain).value shouldBe "err"
            vm.resetPasswordState.value shouldBe ResetPasswordEvent.Idle
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("Continue as guest writes preference (dontAskAgain=true -> shouldShow=false)") {
        val vm = buildVM()
        coEvery { setGuestDialogUC.invoke(false) } returns AppResult.Success(Unit)

        vm.onEvent(AuthenticationUIEvent.OnContinueAsGuestConfirmed(dontAskAgain = true))
        pump()

        coVerify(exactly = 1) { setGuestDialogUC.invoke(false) }
    }

    test("Google social sign-in -> success emits OnSignInSuccess") {
        val vm = buildVM()
        val credential = mockk<AuthCredential>(relaxed = true)
        coEvery { googleClient.getCredential() } returns credential

        coEvery { unifiedSignInUC.invoke(any()) } coAnswers {
            delay(1)
            AppResult.Success(SignInSuccess)
        }

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(AuthenticationUIEvent.OnGoogleButtonClicked)
            pump()
            vm.signInState.value shouldBe SignInEvent.Loading

            scheduler.advanceTimeBy(1); pump()

            awaitItem() shouldBe AuthUiEventModel.OnSignInSuccess
            vm.signInState.value shouldBe SignInEvent.Idle

            coVerify {
                unifiedSignInUC.invoke(
                    UnifiedSignInUseCase.Params.SocialMedia(credential, ProviderType.GOOGLE)
                )
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("Apple social sign-in -> no credential -> ShowError and allow retry (isSignIn=false)") {
        val vm = buildVM()
        coEvery { appleClient.getCredential() } returns null

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(AuthenticationUIEvent.OnAppleButtonClicked)
            pump()
            val ev = awaitItem() as AuthUiEventModel.ShowError
            (ev.error as UIText.Plain).value shouldBe "Unable to get credentials"
            vm.isSignIn shouldBe false
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { unifiedSignInUC.invoke(any()) }
    }

    test("Facebook sign-in uses fetched credential (does not call client.getCredential)") {
        val vm = buildVM()
        val fbCred = mockk<AuthCredential>(relaxed = true)

        coEvery { unifiedSignInUC.invoke(any()) } coAnswers {
            delay(1)
            AppResult.Success(SignInSuccess)
        }

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(AuthenticationUIEvent.OnFacebookButtonClicked(credential = fbCred))
            pump()
            vm.signInState.value shouldBe SignInEvent.Loading

            scheduler.advanceTimeBy(1); pump()

            awaitItem() shouldBe AuthUiEventModel.OnSignInSuccess
            vm.signInState.value shouldBe SignInEvent.Idle

            coVerify(exactly = 0) { facebookClient.getCredential() }
            coVerify {
                unifiedSignInUC.invoke(
                    UnifiedSignInUseCase.Params.SocialMedia(fbCred, ProviderType.FACEBOOK)
                )
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
})
