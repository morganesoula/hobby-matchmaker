package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.fakes.FakeAuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.fakes.FakeSessionRepository
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SignInUseCaseTest : FunSpec({

    val dispatcher = StandardTestDispatcher()
    val testEmail = "john.doe@example.com"
    val testPassword = "password123"

    val fakeSessionRepository = FakeSessionRepository()
    val fakeAuthenticationRepository = FakeAuthenticationRepository(fakeSessionRepository)
    val fakeSetIsConnectedUseCase = mockk<SetIsConnectedUseCase>()
    val useCase = SignInUseCase(dispatcher, fakeAuthenticationRepository, fakeSetIsConnectedUseCase)

    test("should emit Success when sign-in is successful") {
        runTest(dispatcher) {
            coEvery { fakeSetIsConnectedUseCase(any()) } just Runs
            val results =
                useCase.execute(Parameters.DoubleStringParam(testEmail, testPassword)).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Success(SignInSuccess)
            )
        }
    }

    test("should emit Failure.WrongPassword when password is empty") {
        runTest(dispatcher) {
            val results =
                useCase.execute(Parameters.DoubleStringParam(testEmail, "")).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(SignInErrorHMM.WrongPassword)
            )
        }
    }

    test("should emit Failure.UserNotFound when email is empty") {
        runTest(dispatcher) {
            val results =
                useCase.execute(Parameters.DoubleStringParam("", testPassword)).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(SignInErrorHMM.UserNotFound)
            )
        }
    }

    test("should emit Failure.UserDisabled when email && password are empty") {
        runTest(dispatcher) {
            val results =
                useCase.execute(Parameters.DoubleStringParam("", "")).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(SignInErrorHMM.UserDisabled)
            )
        }
    }

    test("should emit Failure.Other when email is unknown error") {
        runTest(dispatcher) {
            val results =
                useCase.execute(Parameters.DoubleStringParam("unknown error", testPassword)).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(SignInErrorHMM.Other("Weird error message"))
            )
        }
    }
})
