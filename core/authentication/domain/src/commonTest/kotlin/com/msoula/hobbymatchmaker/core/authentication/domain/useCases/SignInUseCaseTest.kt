package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.fakes.FakeAuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SignInUseCaseTest : StringSpec({

    val dispatcher = StandardTestDispatcher()
    val testEmail = "john.doe@example.com"
    val testPassword = "password123"

    val fakeAuthenticationRepository = FakeAuthenticationRepository()
    val useCase = SignInUseCase(dispatcher, fakeAuthenticationRepository)

    "should emit Success when sign-in is successful" {
        runTest(dispatcher) {
            val results =
                useCase.execute(Parameters.DoubleStringParam(testEmail, testPassword)).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Success(SignInSuccess)
            )
        }
    }

    "should emit Failure when sign-in is error" {
        runTest(dispatcher) {
            fakeAuthenticationRepository.signInResult = Result.Failure(SignInError.Other(""))

            val results =
                useCase.execute(Parameters.DoubleStringParam("", "")).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(SignInError.Other(""))
            )
        }
    }
})
