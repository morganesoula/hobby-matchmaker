package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import com.msoula.hobbymatchmaker.core.login.presentation.MainDispatcherRule
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherRule::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SignUpIntegrationTest : FunSpec({
    /*val dispatcher = MainDispatcherRule().testDispatcher
    lateinit var signUpVM: SignUpViewModel

    val fakeSessionLocalDataSource = FakeSessionLocalDataSource()
    val fakeSessionRemoteDataSource = FakeSessionRemoteDataSource()
    var fakeAuthenticationRemoteDataSource = FakeAuthenticationRemoteDataSource()

    val authenticationRepository = AuthenticationRepositoryImpl(fakeAuthenticationRemoteDataSource)
    val sessionRepository =
        SessionRepositoryImpl(fakeSessionLocalDataSource, fakeSessionRemoteDataSource)

    val validateEmailUseCase = ValidateEmailUseCase()
    val validatePasswordUseCase = ValidatePasswordUseCase()
    val validateFirstNameUseCase = ValidateNameUseCase()
    val loginValidateFormUseCase = LoginValidateFormUseCase(
        validateEmailUseCase,
        validatePasswordUseCase,
        validateFirstNameUseCase
    )

    val createUserUseCase = CreateUserUseCase(sessionRepository)
    val signUpUseCase = SignUpUseCase(authenticationRepository, createUserUseCase, dispatcher)
    val errorMessageProvider = FakeSignUpErrorMessageProvider()

    test("should emit Success when valid form is submitted") {
        runTest {
            signUpVM = SignUpViewModel(
                loginValidateFormUseCase,
                signUpUseCase,
                errorMessageProvider,
                dispatcher,
                this.backgroundScope
            )

            signUpVM.onEvent(AuthenticationUIEvent.OnFirstNameChanged("Morgane"))
            signUpVM.onEvent(AuthenticationUIEvent.OnEmailChanged("morgane@test.fr"))
            signUpVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("azerty123!"))

            signUpVM.onEvent(AuthenticationUIEvent.OnSignUp)
            advanceUntilIdle()

            signUpVM.signUpState.value shouldBe SignUpEvent.Success
        }
    }

    test("should emit Failure when signUp fails") {
        runTest {
            fakeAuthenticationRemoteDataSource = FakeAuthenticationRemoteDataSource(
                createUserWithEmailAndPasswordResult = Result.Failure(
                    CreateUserWithEmailAndPasswordErrorHMM.EmailAlreadyExists
                )
            )

            val localAuthRepo = AuthenticationRepositoryImpl(fakeAuthenticationRemoteDataSource)
            val localSignUpUseCase = SignUpUseCase(localAuthRepo, createUserUseCase, dispatcher)

            signUpVM = SignUpViewModel(
                loginValidateFormUseCase,
                localSignUpUseCase,
                errorMessageProvider,
                dispatcher,
                this.backgroundScope
            )

            signUpVM.onEvent(AuthenticationUIEvent.OnFirstNameChanged("Morgane"))
            signUpVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test@test.fr"))
            signUpVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("azerty123!"))
            signUpVM.onEvent(AuthenticationUIEvent.OnSignUp)
            advanceUntilIdle()

            signUpVM.signUpState.value shouldBe SignUpEvent.Error("Address already exists")
        }
    }*/
})
