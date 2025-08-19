package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepositoryImpl
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidatePasswordUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.MainDispatcherRule
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes.FakeAuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes.FakeSessionLocalDataSource
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes.FakeSessionRemoteDataSource
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.fakes.FakeSignUpErrorMessageProvider
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepositoryImpl
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherRule::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SignUpIntegrationTest : FunSpec({
    val dispatcher = MainDispatcherRule().testDispatcher
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
    }
})
