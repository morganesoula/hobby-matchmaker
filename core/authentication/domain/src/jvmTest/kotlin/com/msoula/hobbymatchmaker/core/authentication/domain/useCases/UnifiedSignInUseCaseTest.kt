package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.fakes.FakeAuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.fakes.FakeSessionRepository
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class UnifiedSignInUseCaseTest : StringSpec({

    val testEmail = "john.doe@example.com"
    val testPassword = "password123"

    val dispatcher = StandardTestDispatcher()
    val fakeSessionRepository = FakeSessionRepository()
    val fakeAuthenticationRepository = FakeAuthenticationRepository(fakeSessionRepository)
    val setIsConnectedUseCase = SetIsConnectedUseCase(fakeSessionRepository)
    val signInUseCase =
        SignInUseCase(dispatcher, fakeAuthenticationRepository, setIsConnectedUseCase)
    val signInWithCredentialUseCase = SignInWithCredentialUseCase(fakeAuthenticationRepository)
    val useCase = UnifiedSignInUseCase(
        dispatcher,
        signInUseCase,
        signInWithCredentialUseCase,
        setIsConnectedUseCase
    )

    "should emit Success when sign in with email and password is successful" {
        runTest(dispatcher) {
            val results =
                useCase.signIn(UnifiedSignInUseCase.Params.EmailPassword(testEmail, testPassword))
                    .toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Success(SignInSuccess)
            )
        }
    }

    "should emit Failure.WrongPassword with emailPassword param when password is empty" {
        runTest(dispatcher) {
            val results =
                useCase.signIn(UnifiedSignInUseCase.Params.EmailPassword(testEmail, "")).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(SignInErrorHMM.WrongPassword)
            )
        }
    }
})
