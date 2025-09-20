package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import com.msoula.hobbymatchmaker.core.login.presentation.MainDispatcherRule
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherRule::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SignInIntegrationTest : FunSpec({
    /* val dispatcher = MainDispatcherRule().testDispatcher
    lateinit var signInVM: SignInViewModel

    val authenticationRemoteDataSource = FakeAuthenticationRemoteDataSource()
    val sessionRemoteDataSource = FakeSessionRemoteDataSource()
    val sessionLocalDataSource = FakeSessionLocalDataSource()

    val authenticationRepository = AuthenticationRepositoryImpl(authenticationRemoteDataSource)
    val sessionRepository = SessionRepositoryImpl(sessionLocalDataSource, sessionRemoteDataSource)

    val authFormValidationUseCase = AuthFormValidationUseCase(
        ValidatePasswordUseCase(),
        ValidateEmailUseCase(),
        ValidateNameUseCase(),
        ValidateNameUseCase()
    )
    var resetPasswordUseCase = ResetPasswordUseCase(authenticationRepository, dispatcher)
    val setIsConnectedUseCase = SetIsConnectedUseCase(sessionRepository)
    var signInUseCase = SignInUseCase(dispatcher, authenticationRepository, setIsConnectedUseCase)
    var signInWithCredentialUseCase = SignInWithCredentialUseCase(authenticationRepository)
    var unifiedSignInUseCase =
        UnifiedSignInUseCase(
            dispatcher,
            signInUseCase,
            signInWithCredentialUseCase,
            setIsConnectedUseCase
        )
    val socialClients = mapOf<ProviderType, SocialUIClient>()
    val errorMessageProvider = FakeSignInErrorMessageProvider()

    test("should emit SignInEvent.Success when form is valid") {
        signInVM = SignInViewModel(
            authFormValidationUseCase,
            resetPasswordUseCase,
            unifiedSignInUseCase,
            socialClients,
            dispatcher,
            errorMessageProvider,
            this
        )

        runTest {
            signInVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test@test.fr"))
            signInVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("azerty123!"))
            signInVM.onEvent(AuthenticationUIEvent.OnSignIn)
            advanceUntilIdle()

            signInVM.signInState.test {
                awaitItem() shouldBe SignInEvent.Success
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("should emit SignInEvent.Error when password is wrong") {
        runTest {
            val authRemoteDataSource = FakeAuthenticationRemoteDataSource(
                signInWithEmailAndPasswordResult = Result.Failure(SignInWithEmailAndPasswordErrorHMM.WrongPassword)
            )

            val authRepository = AuthenticationRepositoryImpl(authRemoteDataSource)
            resetPasswordUseCase = ResetPasswordUseCase(authRepository, dispatcher)
            signInUseCase = SignInUseCase(dispatcher, authRepository, setIsConnectedUseCase)
            signInWithCredentialUseCase = SignInWithCredentialUseCase(authRepository)
            unifiedSignInUseCase =
                UnifiedSignInUseCase(
                    dispatcher,
                    signInUseCase,
                    signInWithCredentialUseCase,
                    setIsConnectedUseCase
                )

            signInVM = SignInViewModel(
                authFormValidationUseCase,
                resetPasswordUseCase,
                unifiedSignInUseCase,
                socialClients,
                dispatcher,
                errorMessageProvider
            )

            signInVM.signInState.test {
                signInVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test@test.fr"))
                signInVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("wrongPassword123!"))
                signInVM.onEvent(AuthenticationUIEvent.OnSignIn)
                advanceUntilIdle()

                awaitItem() shouldBe SignInEvent.Idle
                awaitItem() shouldBe SignInEvent.Error("wrong password error")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("should emit SignInEvent.Error when user is not found") {
        runTest {
            val authRemoteDataSource = FakeAuthenticationRemoteDataSource(
                signInWithEmailAndPasswordResult = Result.Failure(SignInWithEmailAndPasswordErrorHMM.UserNotFound)
            )

            val authRepository = AuthenticationRepositoryImpl(authRemoteDataSource)
            resetPasswordUseCase = ResetPasswordUseCase(authRepository, dispatcher)
            signInUseCase = SignInUseCase(dispatcher, authRepository, setIsConnectedUseCase)
            signInWithCredentialUseCase = SignInWithCredentialUseCase(authRepository)
            unifiedSignInUseCase =
                UnifiedSignInUseCase(
                    dispatcher,
                    signInUseCase,
                    signInWithCredentialUseCase,
                    setIsConnectedUseCase
                )

            signInVM = SignInViewModel(
                authFormValidationUseCase,
                resetPasswordUseCase,
                unifiedSignInUseCase,
                socialClients,
                dispatcher,
                errorMessageProvider
            )

            signInVM.onEvent(AuthenticationUIEvent.OnEmailChanged("test@test.fr"))
            signInVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("userNotFound123!"))
            signInVM.onEvent(AuthenticationUIEvent.OnSignIn)
            advanceUntilIdle()

            signInVM.signInState.test {
                awaitItem() shouldBe SignInEvent.Error("user not found")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("should emit ResetPasswordEvent.Error when reset fails") {
        runTest {
            val fakeDataSource = FakeAuthenticationRemoteDataSource(
                resetPasswordResult = Result.Failure(ResetPasswordErrorHMM.Other)
            )

            val repo = AuthenticationRepositoryImpl(fakeDataSource)
            val useCase = ResetPasswordUseCase(repo, dispatcher)

            signInVM = SignInViewModel(
                authFormValidationUseCase,
                useCase,
                unifiedSignInUseCase,
                socialClients,
                dispatcher,
                errorMessageProvider
            )

            signInVM.onEvent(AuthenticationUIEvent.OnEmailResetChanged("test@test.fr"))
            signInVM.formDataFlow.value.submitEmailReset shouldBe true

            signInVM.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)

            signInVM.resetPasswordState.test {
                awaitItem() shouldBe ResetPasswordEvent.Error("unknown error while resetting")
                cancelAndIgnoreRemainingEvents()
            }
        }
    } */
})
