package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthenticationRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepositoryImpl
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInWithCredentialUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.UnifiedSignInUseCase
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.di.domain.useCases.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidatePasswordUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.ResetPasswordEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes.FakeAuthManager
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes.FakeSignInErrorMessageProvider
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes.FakeSocialClient
import com.msoula.hobbymatchmaker.core.session.domain.errors.SessionErrors
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class SignInIntegrationTest : FunSpec({
    val dispatcher = UnconfinedTestDispatcher()
    lateinit var signInVM: SignInViewModel

    val validateEmailUseCase = ValidateEmailUseCase()
    val validatePasswordUseCase = ValidatePasswordUseCase()
    val validateFirstNameUseCase = ValidateNameUseCase()
    val authFormValidationFormUseCase = AuthFormValidationUseCase(
        validatePasswordUseCase,
        validateEmailUseCase,
        validateFirstNameUseCase,
        validateFirstNameUseCase
    )

    val errorMessageProvider = FakeSignInErrorMessageProvider()

    test("should emit Success when valid authentication form is submitted") {
        val authenticationRepository = object : AuthenticationRepository {
            override suspend fun signUp(
                email: String,
                password: String
            ): Result<String, CreateUserWithEmailAndPasswordError> =
                Result.Success("fake_uid")

            override suspend fun logOut(): Result<Boolean, LogOutError> = Result.Success(true)

            override suspend fun signInWithEmailAndPassword(
                email: String,
                password: String
            ): Result<String, SignInWithEmailAndPasswordError> = Result.Success("")

            override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError> =
                Result.Success(true)

            override suspend fun signInWithCredential(
                authCredential: AuthCredential,
                providerType: ProviderType
            ): Result<FirebaseUserInfoDomainModel, SocialMediaError> =
                Result.Failure(SocialMediaError.SignInWithCredentialsError)

            override suspend fun linkInWithCredential(authCredential: AuthCredential)
                : Result<FirebaseUserInfoDomainModel, SocialMediaError> =
                Result.Failure(SocialMediaError.LinkWithCredentialsError)

            override suspend fun isFirstSignIn(uid: String): Boolean = true
            override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel? = null
        }

        val sessionRepository = object : SessionRepository {
            override suspend fun setIsConnected(isConnected: Boolean) {}

            override fun observeIsConnected(): Flow<Boolean> = flowOf(true)

            override suspend fun createUser(user: SessionUserDomainModel)
                : Result<Boolean, SessionErrors.CreateUserError> =
                Result.Success(true)
        }

        val signInUseCase = SignInUseCase(dispatcher, authenticationRepository)
        val signInWithCredentialUseCase = SignInWithCredentialUseCase(authenticationRepository)
        val setIsConnectedUseCase = SetIsConnectedUseCase(sessionRepository)

        val resetPasswordUseCase = ResetPasswordUseCase(authenticationRepository, dispatcher)
        val unifiedSignInUseCase =
            UnifiedSignInUseCase(signInUseCase, signInWithCredentialUseCase, setIsConnectedUseCase)

        runTest {
            Dispatchers.setMain(dispatcher)

            signInVM = SignInViewModel(
                authFormValidationFormUseCase,
                resetPasswordUseCase,
                unifiedSignInUseCase,
                emptyMap(),
                dispatcher,
                errorMessageProvider
            )

            try {
                signInVM.onEvent(AuthenticationUIEvent.OnEmailChanged("morgane@test.fr"))
                signInVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("azerty123!"))

                advanceTimeBy(300)
                runCurrent()

                signInVM.onEvent(AuthenticationUIEvent.OnSignIn)

                eventually(2.seconds) {
                    signInVM.signInState.value shouldBe SignInEvent.Success
                }

                signInVM.circularProgressLoading.value shouldBe false
                signInVM.isSignIn shouldBe false
                signInVM.formDataFlow.value.submit shouldBe true
            } finally {
                Dispatchers.resetMain()
            }
        }
    }

    test("should emit WrongPassword when identification fails") {
        val authenticationRepository = object : AuthenticationRepository {
            override suspend fun signUp(
                email: String,
                password: String
            ): Result<String, CreateUserWithEmailAndPasswordError> =
                Result.Success("fake_uid")

            override suspend fun logOut(): Result<Boolean, LogOutError> = Result.Success(true)

            override suspend fun signInWithEmailAndPassword(
                email: String,
                password: String
            ): Result<String, SignInWithEmailAndPasswordError> =
                Result.Failure(SignInWithEmailAndPasswordError.WrongPassword)

            override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError> =
                Result.Success(true)

            override suspend fun signInWithCredential(
                authCredential: AuthCredential,
                providerType: ProviderType
            ): Result<FirebaseUserInfoDomainModel, SocialMediaError> =
                Result.Failure(SocialMediaError.SignInWithCredentialsError)

            override suspend fun linkInWithCredential(authCredential: AuthCredential)
                : Result<FirebaseUserInfoDomainModel, SocialMediaError> =
                Result.Failure(SocialMediaError.LinkWithCredentialsError)

            override suspend fun isFirstSignIn(uid: String): Boolean = true
            override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel? = null
        }

        val sessionRepository = object : SessionRepository {
            override suspend fun setIsConnected(isConnected: Boolean) {}

            override fun observeIsConnected(): Flow<Boolean> = flowOf(true)

            override suspend fun createUser(user: SessionUserDomainModel)
                : Result<Boolean, SessionErrors.CreateUserError> =
                Result.Success(true)
        }

        val signInUseCase = SignInUseCase(dispatcher, authenticationRepository)
        val signInWithCredentialUseCase = SignInWithCredentialUseCase(authenticationRepository)
        val setIsConnectedUseCase = SetIsConnectedUseCase(sessionRepository)

        val resetPasswordUseCase = ResetPasswordUseCase(authenticationRepository, dispatcher)
        val unifiedSignInUseCase =
            UnifiedSignInUseCase(signInUseCase, signInWithCredentialUseCase, setIsConnectedUseCase)

        runTest {
            Dispatchers.setMain(dispatcher)

            signInVM = SignInViewModel(
                authFormValidationFormUseCase,
                resetPasswordUseCase,
                unifiedSignInUseCase,
                emptyMap(),
                dispatcher,
                errorMessageProvider
            )

            try {
                signInVM.onEvent(AuthenticationUIEvent.OnEmailChanged("morgane@test.fr"))
                signInVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("wrongPassword"))

                advanceTimeBy(300)
                runCurrent()

                signInVM.onEvent(AuthenticationUIEvent.OnSignIn)

                eventually(2.seconds) {
                    signInVM.signInState.value shouldBe SignInEvent.Error("wrong password error")
                }

                signInVM.circularProgressLoading.value shouldBe false
                signInVM.isSignIn shouldBe false
            } finally {
                Dispatchers.resetMain()
            }
        }
    }

    test("should resetPassword when form is submitted") {
        val authenticationRepository = object : AuthenticationRepository {
            override suspend fun signUp(
                email: String,
                password: String
            ): Result<String, CreateUserWithEmailAndPasswordError> =
                Result.Success("fake_uid")

            override suspend fun logOut(): Result<Boolean, LogOutError> = Result.Success(true)

            override suspend fun signInWithEmailAndPassword(
                email: String,
                password: String
            ): Result<String, SignInWithEmailAndPasswordError> =
                Result.Failure(SignInWithEmailAndPasswordError.WrongPassword)

            override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError> =
                Result.Success(true)

            override suspend fun signInWithCredential(
                authCredential: AuthCredential,
                providerType: ProviderType
            ): Result<FirebaseUserInfoDomainModel, SocialMediaError> =
                Result.Failure(SocialMediaError.SignInWithCredentialsError)

            override suspend fun linkInWithCredential(authCredential: AuthCredential)
                : Result<FirebaseUserInfoDomainModel, SocialMediaError> =
                Result.Failure(SocialMediaError.LinkWithCredentialsError)

            override suspend fun isFirstSignIn(uid: String): Boolean = true
            override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel? = null
        }

        val sessionRepository = object : SessionRepository {
            override suspend fun setIsConnected(isConnected: Boolean) {}

            override fun observeIsConnected(): Flow<Boolean> = flowOf(true)

            override suspend fun createUser(user: SessionUserDomainModel)
                : Result<Boolean, SessionErrors.CreateUserError> =
                Result.Success(true)
        }

        val signInUseCase = SignInUseCase(dispatcher, authenticationRepository)
        val signInWithCredentialUseCase = SignInWithCredentialUseCase(authenticationRepository)
        val setIsConnectedUseCase = SetIsConnectedUseCase(sessionRepository)

        val resetPasswordUseCase = ResetPasswordUseCase(authenticationRepository, dispatcher)
        val unifiedSignInUseCase =
            UnifiedSignInUseCase(signInUseCase, signInWithCredentialUseCase, setIsConnectedUseCase)

        runTest {
            Dispatchers.setMain(dispatcher)

            signInVM = SignInViewModel(
                authFormValidationFormUseCase,
                resetPasswordUseCase,
                unifiedSignInUseCase,
                emptyMap(),
                dispatcher,
                errorMessageProvider
            )

            try {
                signInVM.onEvent(
                    AuthenticationUIEvent.OnEmailResetChanged(
                        "testReset@test.fr"
                    )
                )

                signInVM.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
                signInVM.formDataFlow.value.submitEmailReset shouldBe true
                advanceUntilIdle()

                signInVM.formDataFlow.value.emailReset shouldBe ""
                signInVM.resetPasswordState.value shouldBe ResetPasswordEvent.Success
            } finally {
                Dispatchers.resetMain()
            }
        }
    }

    test("should not reset password when email doesn't exist") {
        val authenticationRepository = object : AuthenticationRepository {
            override suspend fun signUp(
                email: String,
                password: String
            ): Result<String, CreateUserWithEmailAndPasswordError> =
                Result.Success("fake_uid")

            override suspend fun logOut(): Result<Boolean, LogOutError> = Result.Success(true)

            override suspend fun signInWithEmailAndPassword(
                email: String,
                password: String
            ): Result<String, SignInWithEmailAndPasswordError> =
                Result.Failure(SignInWithEmailAndPasswordError.WrongPassword)

            override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError> =
                Result.Failure(ResetPasswordError.Other)

            override suspend fun signInWithCredential(
                authCredential: AuthCredential,
                providerType: ProviderType
            ): Result<FirebaseUserInfoDomainModel, SocialMediaError> =
                Result.Failure(SocialMediaError.SignInWithCredentialsError)

            override suspend fun linkInWithCredential(authCredential: AuthCredential)
                : Result<FirebaseUserInfoDomainModel, SocialMediaError> =
                Result.Failure(SocialMediaError.LinkWithCredentialsError)

            override suspend fun isFirstSignIn(uid: String): Boolean = true
            override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel? = null
        }

        val sessionRepository = object : SessionRepository {
            override suspend fun setIsConnected(isConnected: Boolean) {}

            override fun observeIsConnected(): Flow<Boolean> = flowOf(true)

            override suspend fun createUser(user: SessionUserDomainModel)
                : Result<Boolean, SessionErrors.CreateUserError> =
                Result.Success(true)
        }

        val signInUseCase = SignInUseCase(dispatcher, authenticationRepository)
        val signInWithCredentialUseCase = SignInWithCredentialUseCase(authenticationRepository)
        val setIsConnectedUseCase = SetIsConnectedUseCase(sessionRepository)

        val resetPasswordUseCase = ResetPasswordUseCase(authenticationRepository, dispatcher)
        val unifiedSignInUseCase =
            UnifiedSignInUseCase(signInUseCase, signInWithCredentialUseCase, setIsConnectedUseCase)

        runTest {
            Dispatchers.setMain(dispatcher)

            signInVM = SignInViewModel(
                authFormValidationFormUseCase,
                resetPasswordUseCase,
                unifiedSignInUseCase,
                emptyMap(),
                dispatcher,
                errorMessageProvider
            )

            try {
                signInVM.onEvent(
                    AuthenticationUIEvent.OnEmailResetChanged(
                        "testReset@test.fr"
                    )
                )

                signInVM.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
                signInVM.formDataFlow.value.submitEmailReset shouldBe true
                advanceUntilIdle()

                signInVM.formDataFlow.value.emailReset shouldBe "testReset@test.fr"
                signInVM.resetPasswordState.value shouldBe
                    ResetPasswordEvent.Error("unknown error while resetting")
            } finally {
                Dispatchers.resetMain()
            }
        }
    }

    test("should login with FakeGoogle when client exists") {
        val fakeSocialClient = FakeSocialClient()
        val fakeAuthManager = FakeAuthManager()

        val remote = AuthenticationRemoteDataSourceImpl(
            auth = mockk<FirebaseAuth>(),
            firestore = mockk<FirebaseFirestore>(),
            authManager = fakeAuthManager
        )
        val repository = AuthenticationRepositoryImpl(remote)

        val sessionRepository = object : SessionRepository {
            override suspend fun setIsConnected(isConnected: Boolean) {}

            override fun observeIsConnected(): Flow<Boolean> = flowOf(true)

            override suspend fun createUser(user: SessionUserDomainModel)
                : Result<Boolean, SessionErrors.CreateUserError> =
                Result.Success(true)
        }

        val signInUseCase = SignInUseCase(dispatcher, repository)
        val signInWithCredentialUseCase = SignInWithCredentialUseCase(repository)
        val setIsConnectedUseCase = SetIsConnectedUseCase(sessionRepository)
        val resetPasswordUseCase = ResetPasswordUseCase(repository, dispatcher)

        val unifiedSignInUseCase =
            UnifiedSignInUseCase(signInUseCase, signInWithCredentialUseCase, setIsConnectedUseCase)

        val socialClients = mapOf(ProviderType.GOOGLE to fakeSocialClient)

        runTest {
            val testScope = this
            val localDispatcher = StandardTestDispatcher(testScheduler)
            Dispatchers.setMain(localDispatcher)

            signInVM = SignInViewModel(
                authFormValidationFormUseCase,
                resetPasswordUseCase,
                unifiedSignInUseCase,
                socialClients,
                localDispatcher,
                errorMessageProvider,
                externalScope = testScope
            )

            try {
                signInVM.onEvent(AuthenticationUIEvent.OnGoogleButtonClicked)

                advanceUntilIdle()
                runCurrent()

                eventually(2.seconds) {
                    signInVM.signInState.value shouldBe SignInEvent.Success
                    signInVM.isSignIn shouldBe false
                }
            } finally {
                Dispatchers.resetMain()
            }
        }
    }

    test("should emit error when FakeGoogle fails") {
        val fakeSocialClient = FakeSocialClient()
        val fakeAuthManager = FakeAuthManager(true)

        val remote = AuthenticationRemoteDataSourceImpl(
            auth = mockk<FirebaseAuth>(),
            firestore = mockk<FirebaseFirestore>(),
            authManager = fakeAuthManager
        )
        val repository = AuthenticationRepositoryImpl(remote)

        val sessionRepository = object : SessionRepository {
            override suspend fun setIsConnected(isConnected: Boolean) {}

            override fun observeIsConnected(): Flow<Boolean> = flowOf(true)

            override suspend fun createUser(user: SessionUserDomainModel)
                : Result<Boolean, SessionErrors.CreateUserError> =
                Result.Success(true)
        }

        val signInUseCase = SignInUseCase(dispatcher, repository)
        val signInWithCredentialUseCase = SignInWithCredentialUseCase(repository)
        val setIsConnectedUseCase = SetIsConnectedUseCase(sessionRepository)
        val resetPasswordUseCase = ResetPasswordUseCase(repository, dispatcher)

        val unifiedSignInUseCase =
            UnifiedSignInUseCase(signInUseCase, signInWithCredentialUseCase, setIsConnectedUseCase)

        val socialClients = mapOf(ProviderType.GOOGLE to fakeSocialClient)

        runTest {
            val testScope = this
            val localDispatcher = StandardTestDispatcher(testScheduler)
            Dispatchers.setMain(localDispatcher)

            signInVM = SignInViewModel(
                authFormValidationFormUseCase,
                resetPasswordUseCase,
                unifiedSignInUseCase,
                socialClients,
                localDispatcher,
                errorMessageProvider,
                externalScope = testScope
            )

            try {
                signInVM.onEvent(AuthenticationUIEvent.OnGoogleButtonClicked)

                advanceUntilIdle()
                runCurrent()

                eventually(2.seconds) {
                    signInVM.signInState.value shouldBe SignInEvent.Error("Simulated sign-in error")
                    signInVM.isSignIn shouldBe false
                }
            } finally {
                Dispatchers.resetMain()
            }
        }
    }
})
