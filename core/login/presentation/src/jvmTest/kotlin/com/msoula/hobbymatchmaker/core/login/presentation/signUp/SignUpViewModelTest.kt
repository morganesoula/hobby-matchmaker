package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.UIText
import com.msoula.hobbymatchmaker.core.common.data.ValidationResult
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthUiEventModel
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest : FunSpec({
    val scheduler = TestCoroutineScheduler()
    val dispatcher = StandardTestDispatcher(scheduler)
    val testScope = TestScope(dispatcher + Job())

    lateinit var validateUC: LoginValidateFormUseCase
    lateinit var signUpUC: SignUpUseCase
    val errorMapper = object : ErrorMessageMapper {
        override fun toUIText(error: AppError): UIText {
            return UIText.Plain("err")
        }
    }

    fun elapse(ms: Long) {
        scheduler.advanceTimeBy(ms)
        scheduler.runCurrent()
    }

    fun pump() = scheduler.runCurrent()

    fun buildVM(): SignUpViewModel {
        validateUC = mockk(relaxed = true)
        signUpUC = mockk(relaxed = true)

        every { validateUC.validateEmail(any()) } returns ValidationResult(
            successful = true,
            errorMessage = null
        )
        every { validateUC.validateFirstName(any()) } returns ValidationResult(
            successful = true,
            errorMessage = null
        )
        every { validateUC.validatePassword.validatePassword(any()) } returns ValidationResult(
            successful = true,
            errorMessage = null
        )

        return SignUpViewModel(
            loginValidateFormUseCase = validateUC,
            signUpUseCase = signUpUC,
            defaultErrorMessageMapper = errorMapper,
            externalScope = testScope
        )
    }

    beforeSpec { Dispatchers.setMain(dispatcher) }
    afterSpec { Dispatchers.resetMain(); unmockkAll() }
    beforeTest { clearAllMocks() }

    test("initial signUpState is Idle") {
        val vm = buildVM()
        vm.signUpState.value shouldBe SignUpEvent.Idle
    }

    test("debounced validation sets submit=true when all fields valid") {
        val vm = buildVM()

        vm.onEvent(AuthenticationUIEvent.OnEmailChanged("  mail@example.com  "))
        vm.onEvent(AuthenticationUIEvent.OnFirstNameChanged("  Jane  "))
        vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("  Secret123  "))

        elapse(300)

        val form = vm.formDataFlow.value
        form.email shouldBe "mail@example.com"
        form.firstName shouldBe "Jane"
        form.password shouldBe "Secret123"
        form.submit.shouldBeTrue()
        form.signUpError shouldBe ""
    }

    test("debounced validation sets signUpError when firstName invalid") {
        val vm = buildVM()

        every { validateUC.validateFirstName("A") } returns
            ValidationResult(successful = false, errorMessage = "First name too short")

        vm.onEvent(AuthenticationUIEvent.OnEmailChanged("mail@example.com"))
        vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("Secret123"))
        vm.onEvent(AuthenticationUIEvent.OnFirstNameChanged("A"))

        elapse(300)

        val form = vm.formDataFlow.value
        form.submit.shouldBeFalse()
        form.signUpError shouldBe "First name too short"
    }

    test("OnSignUp success -> emits OnSignUpSuccess and state back to Idle; args are trimmed") {
        val vm = buildVM()

        coEvery { signUpUC.invoke(any()) } coAnswers {
            delay(1)
            AppResult.Success(SignUpSuccess)
        }

        vm.onEvent(AuthenticationUIEvent.OnEmailChanged("  mail@example.com  "))
        vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("  Secret123  "))
        vm.onEvent(AuthenticationUIEvent.OnFirstNameChanged("  Jane  "))
        elapse(300)

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(AuthenticationUIEvent.OnSignUp)
            pump()

            vm.signUpState.value shouldBe SignUpEvent.Loading
            scheduler.advanceTimeBy(1)
            pump()

            awaitItem() shouldBe AuthUiEventModel.OnSignUpSuccess
            vm.signUpState.value shouldBe SignUpEvent.Idle

            coVerify(exactly = 1) {
                signUpUC.invoke(
                    Parameters.DoubleStringParam("mail@example.com", "Secret123")
                )
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("OnSignUp failure -> emits ShowError(err) and state back to Idle") {
        val vm = buildVM()

        coEvery { signUpUC.invoke(any()) } coAnswers {
            delay(1)
            AppResult.Failure(AppError.Domain.Forbidden)
        }

        vm.onEvent(AuthenticationUIEvent.OnEmailChanged("mail@example.com"))
        vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("Secret123"))
        vm.onEvent(AuthenticationUIEvent.OnFirstNameChanged("Jane"))
        elapse(300)

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(AuthenticationUIEvent.OnSignUp)
            pump()

            vm.signUpState.value shouldBe SignUpEvent.Loading
            scheduler.advanceTimeBy(1)
            pump()

            val ev = awaitItem() as AuthUiEventModel.ShowError
            (ev.error as UIText.Plain).value shouldBe "err"
            vm.signUpState.value shouldBe SignUpEvent.Idle
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("OnScreenChanged resets form to defaults") {
        val vm = buildVM()

        vm.onEvent(AuthenticationUIEvent.OnEmailChanged("mail@example.com"))
        vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("Secret123"))
        vm.onEvent(AuthenticationUIEvent.OnFirstNameChanged("Jane"))
        elapse(300)

        vm.onEvent(AuthenticationUIEvent.OnScreenChanged)
        pump()

        val reset = vm.formDataFlow.value
        reset.email shouldBe ""
        reset.password shouldBe ""
        reset.firstName shouldBe ""
        reset.submit.shouldBeFalse()
        reset.signUpError shouldBe null
    }
})
