package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthUiEventModel
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.models.SignUpStateModel
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import com.msoula.hobbymatchmaker.core.common.UIText
import com.msoula.hobbymatchmaker.core.common.UIText.Plain
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import org.koin.core.context.stopKoin
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.dsl.bind
import org.koin.core.module.Module
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.extension.RegisterExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

@OptIn(ExperimentalCoroutinesApi::class)
@TestInstance(Lifecycle.PER_CLASS)
class SignUpIntegrationTest : FunSpec({

    val dispatcher = UnconfinedTestDispatcher()
    val testScope = TestScope(dispatcher)

    class FakeAuthenticationRepository : AuthenticationRepository {
        var signUpResult: AppResult<String, AppError> = AppResult.Success("uid-123")
        override suspend fun logOut(): AppResult<Unit, AppError> = AppResult.Success(Unit)
        override suspend fun signUp(email: String, password: String): AppResult<String, AppError> =
            signUpResult
        override suspend fun signInWithEmailAndPassword(email: String, password: String)
            : AppResult<String, AppError> = error("not used in this test")
        override suspend fun resetPassword(email: String): AppResult<Unit, AppError> =
            error("not used in this test")
        override suspend fun signInWithCredential(
            authCredential: dev.gitlive.firebase.auth.AuthCredential,
            providerType: com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
        ) = error("not used in this test")
        override suspend fun linkInWithCredential(
            authCredential: dev.gitlive.firebase.auth.AuthCredential
        ) = error("not used in this test")
        override suspend fun isFirstSignIn(uid: String): AppResult<Boolean, AppError> =
            error("not used in this test")
        override suspend fun fetchFirebaseUserInfo()
            : AppResult<com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState, AppError> =
            error("not used in this test")
    }

    class FakeSessionRepository : SessionRepository {
        var createUserResult: AppResult<Unit, AppError> = AppResult.Success(Unit)
        val createdUsers = mutableListOf<SessionUserDomainModel>()

        override suspend fun setIsConnected(isConnected: Boolean)
            : AppResult<Unit, AppError> = AppResult.Success(Unit)

        override fun observeIsConnected() = MutableStateFlow(false)

        override suspend fun createUser(user: SessionUserDomainModel)
            : AppResult<Unit, AppError> {
            createdUsers += user
            return createUserResult
        }

        override suspend fun setShouldShowGuestDialog(shouldShow: Boolean)
            : AppResult<Unit, AppError> = AppResult.Success(Unit)

        override fun observeShouldShowGuestDialog() = MutableStateFlow(false)
    }

    // Mapper override to avoid StringResource on JVM
    object PlainErrorMessageMapper : ErrorMessageMapper {
        override fun toUIText(error: AppError): UIText = Plain("mapped")
    }

    lateinit var fakeAuthRepo: FakeAuthenticationRepository
    lateinit var fakeSessionRepo: FakeSessionRepository

    // Koin module overrides for DATA & Mapper only
    val testModule: Module = module {
        single<AuthenticationRepository> { FakeAuthenticationRepository().also { fakeAuthRepo = it } }
        single<SessionRepository> { FakeSessionRepository().also { fakeSessionRepo = it } }
        single<ErrorMessageMapper> { PlainErrorMessageMapper }
        // Keep real usecases
        factory { CreateUserUseCase(get()) }
        factory { SignUpUseCase(get(), get()) }
    }

    beforeSpec {
        startKoin {
            modules(
                testModule
                // Note: pas besoin d’inclure tes modules core "data" réels,
                // on override précisément les bindings utilisés par le flux SignUp.
            )
        }
    }

    afterSpec {
        stopKoin()
    }

    test("Happy path: valid form -> OnSignUpSuccess and Idle state") {
        runTest(dispatcher) {
            val vm = SignUpViewModel(
                loginValidateFormUseCase = com.msoula.hobbymatchmaker.core.login.presentation.LoginValidateFormUseCase(
                    com.msoula.hobbymatchmaker.core.login.presentation.ValidateEmailUseCase(),
                    com.msoula.hobbymatchmaker.core.login.presentation.ValidatePasswordUseCase(),
                    com.msoula.hobbymatchmaker.core.login.presentation.ValidateNameUseCase()
                ),
                signUpUseCase = get(),
                defaultErrorMessageMapper = get(),
                externalScope = testScope
            )

            // Fill valid fields
            vm.onEvent(AuthenticationUIEvent.OnFirstNameChanged("Morgane"))
            vm.onEvent(AuthenticationUIEvent.OnEmailChanged("user@mail.com"))
            vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("Abcdef1!"))

            // Wait for debounce (250 ms)
            advanceTimeBy(300)

            // submit should be true
            vm.formDataFlow.value.submit shouldBe true

            // Observe one-time events
            vm.oneTimeEventChannelFlow.test {
                // Trigger sign up
                vm.onEvent(AuthenticationUIEvent.OnSignUp)

                // Loading state then back to Idle
                vm.signUpState.value shouldBe SignUpEvent.Loading
                // Let coroutines finish
                advanceUntilIdle()

                // Expect success event
                awaitItem() shouldBe AuthUiEventModel.OnSignUpSuccess

                // Back to Idle
                vm.signUpState.value shouldBe SignUpEvent.Idle

                // Assert repository interactions
                fakeSessionRepo.createdUsers.size shouldBe 1
                val created = fakeSessionRepo.createdUsers.first()
                created.uid shouldBe "uid-123"
                created.email shouldBe "user@mail.com"

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("Failure on signUp -> ShowError(mapped) and Idle state") {
        runTest(dispatcher) {
            fakeAuthRepo.signUpResult = AppResult.Failure(AppError.Authentication.AlreadyExists)

            val vm = SignUpViewModel(
                loginValidateFormUseCase = com.msoula.hobbymatchmaker.core.login.presentation.LoginValidateFormUseCase(
                    com.msoula.hobbymatchmaker.core.login.presentation.ValidateEmailUseCase(),
                    com.msoula.hobbymatchmaker.core.login.presentation.ValidatePasswordUseCase(),
                    com.msoula.hobbymatchmaker.core.login.presentation.ValidateNameUseCase()
                ),
                signUpUseCase = get(),
                defaultErrorMessageMapper = get(),
                externalScope = testScope
            )

            vm.onEvent(AuthenticationUIEvent.OnFirstNameChanged("Morgane"))
            vm.onEvent(AuthenticationUIEvent.OnEmailChanged("user@mail.com"))
            vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("Abcdef1!"))

            // Wait for debounce to set submit=true (even if VM doesn't gate OnSignUp on submit)
            advanceTimeBy(300)

            vm.oneTimeEventChannelFlow.test {
                vm.onEvent(AuthenticationUIEvent.OnSignUp)

                vm.signUpState.value shouldBe SignUpEvent.Loading
                advanceUntilIdle()

                val event = awaitItem()
                // We only check the type & that we mapped to Plain("mapped")
                (event as AuthUiEventModel.ShowError).error shouldBe Plain("mapped")

                vm.signUpState.value shouldBe SignUpEvent.Idle

                // createUser should NOT be called on signUp failure
                fakeSessionRepo.createdUsers.size shouldBe 0

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("Validation wiring: submit toggles true only when all fields valid") {
        runTest(dispatcher) {
            val vm = SignUpViewModel(
                loginValidateFormUseCase = com.msoula.hobbymatchmaker.core.login.presentation.LoginValidateFormUseCase(
                    com.msoula.hobbymatchmaker.core.login.presentation.ValidateEmailUseCase(),
                    com.msoula.hobbymatchmaker.core.login.presentation.ValidatePasswordUseCase(),
                    com.msoula.hobbymatchmaker.core.login.presentation.ValidateNameUseCase()
                ),
                signUpUseCase = get(),
                defaultErrorMessageMapper = get(),
                externalScope = testScope
            )

            // Start invalid
            vm.formDataFlow.value.submit shouldBe false

            vm.onEvent(AuthenticationUIEvent.OnFirstNameChanged("Morgane"))
            advanceTimeBy(300)
            vm.formDataFlow.value.submit shouldBe false

            vm.onEvent(AuthenticationUIEvent.OnEmailChanged("user@mail.com"))
            advanceTimeBy(300)
            vm.formDataFlow.value.submit shouldBe false // pwd still invalid

            vm.onEvent(AuthenticationUIEvent.OnPasswordChanged("Abcdef1!"))
            advanceTimeBy(300)
            vm.formDataFlow.value.submit shouldBe true
        }
    }
})
