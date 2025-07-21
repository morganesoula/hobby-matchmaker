package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidatePasswordUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.fakes.FakeSignUpErrorMessageProvider
import com.msoula.hobbymatchmaker.core.session.domain.errors.SessionErrors
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import dev.gitlive.firebase.auth.AuthCredential
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpIntegrationTest : FunSpec({
    val dispatcher = StandardTestDispatcher()
    lateinit var signUpVM: SignUpViewModel

    val validateEmailUseCase = ValidateEmailUseCase()
    val validatePasswordUseCase = ValidatePasswordUseCase()
    val validateFirstNameUseCase = ValidateNameUseCase()
    val loginValidateFormUseCase = LoginValidateFormUseCase(
        validateEmailUseCase,
        validatePasswordUseCase,
        validateFirstNameUseCase
    )

    val errorMessageProvider = FakeSignUpErrorMessageProvider()

    test("should emit Success when valid form is submitted") {
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

        val createUserUseCase = CreateUserUseCase(sessionRepository)
        val signUpUseCase = SignUpUseCase(authenticationRepository, createUserUseCase, dispatcher)

        signUpVM = SignUpViewModel(
            loginValidateFormUseCase,
            signUpUseCase,
            errorMessageProvider,
            dispatcher
        )

        runTest(dispatcher) {
            signUpVM.onEvent(AuthenticationUIEvent.OnFirstNameChanged("Morgane"))
            signUpVM.onEvent(AuthenticationUIEvent.OnEmailChanged("morgane@test.fr"))
            signUpVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("azerty123!"))

            signUpVM.onEvent(AuthenticationUIEvent.OnSignUp)
            advanceUntilIdle()

            signUpVM.signUpState.value shouldBe SignUpEvent.Success
        }
    }

    test("should emit Failure when signUp fails") {
        val authenticationRepository = object : AuthenticationRepository {
            override suspend fun signUp(
                email: String,
                password: String
            ): Result<String, CreateUserWithEmailAndPasswordError> =
                Result.Failure(CreateUserWithEmailAndPasswordError.EmailAlreadyExists)

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

        val createUserUseCase = CreateUserUseCase(sessionRepository)
        val signUpUseCase = SignUpUseCase(authenticationRepository, createUserUseCase, dispatcher)

        signUpVM = SignUpViewModel(
            loginValidateFormUseCase,
            signUpUseCase,
            errorMessageProvider,
            dispatcher
        )

        runTest(dispatcher) {
            signUpVM.onEvent(AuthenticationUIEvent.OnFirstNameChanged("Morgane"))
            signUpVM.onEvent(AuthenticationUIEvent.OnEmailChanged("morgane@test.fr"))
            signUpVM.onEvent(AuthenticationUIEvent.OnPasswordChanged("azerty123!"))

            signUpVM.onEvent(AuthenticationUIEvent.OnSignUp)
            advanceUntilIdle()

            signUpVM.signUpState.value shouldBe SignUpEvent.Error("Adresse e-mail déjà utilisée")
        }
    }
})
