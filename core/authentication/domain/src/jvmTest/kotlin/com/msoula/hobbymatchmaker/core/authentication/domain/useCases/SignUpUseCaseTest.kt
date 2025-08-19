package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.fakes.FakeAuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.fakes.FakeSessionRepository
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpUseCaseTest : StringSpec({

    val testEmail = "john.doe@example.com"
    val testPassword = "password123"

    val dispatcher = StandardTestDispatcher()
    val fakeSessionRepository = FakeSessionRepository()
    val fakeAuthenticationRepository = FakeAuthenticationRepository(fakeSessionRepository)

    val createUserUseCase = CreateUserUseCase(fakeSessionRepository)
    val useCase = SignUpUseCase(fakeAuthenticationRepository, createUserUseCase, dispatcher)

    "should emit Success when sign up is successful" {
        runTest(dispatcher) {
            val results =
                useCase.execute(Parameters.DoubleStringParam(testEmail, testPassword)).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Success(SignUpSuccess("fakeUid"))
            )
        }
    }

    "should emit Failure.UserDisabled when email && password are empty" {
        runTest(dispatcher) {
            val results =
                useCase.execute(Parameters.DoubleStringParam("", "")).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(SignUpErrors.UserDisabled)
            )
        }
    }

    "should emit Failure.InternalError when password only is empty" {
        runTest(dispatcher) {
            val results =
                useCase.execute(Parameters.DoubleStringParam(testEmail, "")).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(SignUpErrors.InternalErrorHMM)
            )
        }
    }

    "should emit Failure.TooManyRequests when email only is empty" {
        runTest(dispatcher) {
            val results =
                useCase.execute(Parameters.DoubleStringParam("", testPassword)).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(SignUpErrors.TooManyRequests)
            )
        }
    }

    "should emit Failure.EmailAlreadyExists when email == password" {
        runTest(dispatcher) {
            val results =
                useCase.execute(Parameters.DoubleStringParam(testEmail, testEmail)).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(SignUpErrors.EmailAlreadyExists)
            )
        }
    }

    "should emit Failure.UnknownError when email == unknown error" {
        runTest(dispatcher) {
            val results =
                useCase.execute(Parameters.DoubleStringParam("unknown error", testEmail)).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(SignUpErrors.UnknownErrorHMM("Weird error message"))
            )
        }
    }
})
