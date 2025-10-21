package com.msoula.hobbymatchmaker.tests.integration

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthUiEventModel
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import com.msoula.hobbymatchmaker.core.common.UIText.Plain
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidatePasswordUseCase
import com.msoula.hobbymatchmaker.tests.fakes.FakeAuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.tests.fakes.FakeSessionLocalDataSource
import com.msoula.hobbymatchmaker.tests.fakes.FakeSessionRemoteDataSource
import com.msoula.hobbymatchmaker.tests.modules.signUpTestModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.koin.core.context.stopKoin
import org.koin.core.context.startKoin
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import org.koin.mp.KoinPlatform.getKoin

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpIntegrationTest : FunSpec({

    val dispatcher = StandardTestDispatcher()
    val testScope = TestScope(dispatcher)

    beforeSpec {
        startKoin { modules(signUpTestModule) }
    }

    afterSpec {
        stopKoin()
    }

    beforeTest {
        val fakeSessionRemote: FakeSessionRemoteDataSource = getKoin().get()
        val fakeAuthRemote: FakeAuthenticationRemoteDataSource = getKoin().get()
        val fakeLocal: FakeSessionLocalDataSource = getKoin().get()

        fakeSessionRemote.created.clear()
        fakeSessionRemote.createUserResult = AppResult.Success(Unit)

        fakeAuthRemote.createUserResult = AppResult.Success("uid-123")
        fakeAuthRemote.signUpDelayMs = 0L

        fakeLocal.setIsConnected(false)
        fakeLocal.setShouldShowGuestDialog(true)
    }

    test("Happy path: valid form -> emits OnSignUpSuccess and state returns to Idle; SessionRemoteDataSource receives createUser(uid,email)") {
        runTest(dispatcher) {
            val signUpUc: SignUpUseCase = getKoin().get<SignUpUseCase>()

            val vm = SignUpViewModel(
                loginValidateFormUseCase = LoginValidateFormUseCase(
                    ValidateEmailUseCase(),
                    ValidatePasswordUseCase(),
                    ValidateNameUseCase()
                ),
                signUpUseCase = signUpUc,
                defaultErrorMessageMapper = getKoin().get<ErrorMessageMapper>(),
                externalScope = testScope
            )

            val fakeSessionRemote: FakeSessionRemoteDataSource =
                getKoin().get<FakeSessionRemoteDataSource>()
            val fakeAuthRemote: FakeAuthenticationRemoteDataSource =
                getKoin().get<FakeAuthenticationRemoteDataSource>()
            fakeAuthRemote.createUserResult = AppResult.Success("uid-123")
            fakeAuthRemote.signUpDelayMs = 1_000L

            vm.onEvent(AuthenticationUIEvent.OnFirstNameChanged("Morgane"))
            vm.onEvent(AuthenticationUIEvent.OnEmailChanged("user@mail.com"))
            vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("Abcdef1!"))

            advanceTimeBy(300)
            vm.formDataFlow.value.submit shouldBe true

            vm.oneTimeEventChannelFlow.test {
                vm.onEvent(AuthenticationUIEvent.OnSignUp)

                runCurrent()
                vm.signUpState.value shouldBe SignUpEvent.Loading

                advanceTimeBy(1_000L)
                advanceUntilIdle()

                awaitItem() shouldBe AuthUiEventModel.OnSignUpSuccess
                vm.signUpState.value shouldBe SignUpEvent.Idle

                fakeSessionRemote.created.size shouldBe 1
                val user = fakeSessionRemote.created.first()
                user.uid shouldBe "uid-123"
                user.email shouldBe "user@mail.com"

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("Failure on signUp -> emits ShowError(mapped) and returns to Idle; no user created") {
        runTest(dispatcher) {
            val signUpUc: SignUpUseCase = getKoin().get<SignUpUseCase>()
            val vm = SignUpViewModel(
                loginValidateFormUseCase = LoginValidateFormUseCase(
                    ValidateEmailUseCase(),
                    ValidatePasswordUseCase(),
                    ValidateNameUseCase()
                ),
                signUpUseCase = signUpUc,
                defaultErrorMessageMapper = getKoin().get<ErrorMessageMapper>(),
                externalScope = testScope
            )

            val fakeSessionRemote: FakeSessionRemoteDataSource =
                getKoin().get<FakeSessionRemoteDataSource>()
            val fakeAuthRemote: FakeAuthenticationRemoteDataSource =
                getKoin().get<FakeAuthenticationRemoteDataSource>()
            fakeAuthRemote.createUserResult =
                AppResult.Failure(AppError.Authentication.AlreadyExists)
            fakeAuthRemote.signUpDelayMs = 1_000L

            vm.onEvent(AuthenticationUIEvent.OnFirstNameChanged("Morgane"))
            vm.onEvent(AuthenticationUIEvent.OnEmailChanged("user@mail.com"))
            vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("Abcdef1!"))
            advanceTimeBy(300)

            vm.oneTimeEventChannelFlow.test {
                vm.onEvent(AuthenticationUIEvent.OnSignUp)

                runCurrent()
                vm.signUpState.value shouldBe SignUpEvent.Loading

                advanceTimeBy(1_000L)
                advanceUntilIdle()

                val event = awaitItem()
                (event as AuthUiEventModel.ShowError).error shouldBe Plain("mapped")
                vm.signUpState.value shouldBe SignUpEvent.Idle

                fakeSessionRemote.created.size shouldBe 0

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("Validation: submit becomes true only when firstName, email and password are all valid") {
        runTest(dispatcher) {
            val vm = SignUpViewModel(
                loginValidateFormUseCase = LoginValidateFormUseCase(
                    ValidateEmailUseCase(),
                    ValidatePasswordUseCase(),
                    ValidateNameUseCase()
                ),
                signUpUseCase = getKoin().get<SignUpUseCase>(),
                defaultErrorMessageMapper = getKoin().get<ErrorMessageMapper>(),
                externalScope = testScope
            )

            vm.formDataFlow.value.submit shouldBe false

            vm.onEvent(AuthenticationUIEvent.OnFirstNameChanged("Morgane"))
            advanceTimeBy(300)
            vm.formDataFlow.value.submit shouldBe false

            vm.onEvent(AuthenticationUIEvent.OnEmailChanged("user@mail.com"))
            advanceTimeBy(300)
            vm.formDataFlow.value.submit shouldBe false

            vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("Abcdef1!"))
            advanceTimeBy(300)
            vm.formDataFlow.value.submit shouldBe true
        }
    }
})
