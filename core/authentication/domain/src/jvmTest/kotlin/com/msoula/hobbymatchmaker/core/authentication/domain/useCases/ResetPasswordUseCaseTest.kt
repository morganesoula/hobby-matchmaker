package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.fakes.FakeAuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.fakes.FakeSessionRepository
import com.msoula.hobbymatchmaker.core.common.Parameters
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ResetPasswordUseCaseTest : StringSpec({
    val dispatcher = StandardTestDispatcher()
    val fakeSessionRepository = FakeSessionRepository()
    val fakeAuthenticationRepository = FakeAuthenticationRepository(fakeSessionRepository)
    val useCase = ResetPasswordUseCase(fakeAuthenticationRepository, dispatcher)

    "should emit Success when reset password is successful" {
        runTest(dispatcher) {
            val results = useCase.execute(Parameters.StringParam("john.doe@example.com")).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Success(ResetPasswordSuccess)
            )
        }
    }

    "should emit Failure.Other when email is empty" {
        runTest(dispatcher) {
            val results = useCase.execute(Parameters.StringParam("")).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(ResetPasswordErrorHMM.Other)
            )
        }
    }

    "should emit Failure.TooManyRequests when email == too many requests" {
        runTest(dispatcher) {
            val results = useCase.execute(Parameters.StringParam("too many requests")).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(ResetPasswordErrorHMM.TooManyRequests)
            )
        }
    }

    "should emit Failure.Connection when email == connection issue" {
        runTest(dispatcher) {
            val results = useCase.execute(Parameters.StringParam("connection issue")).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(ResetPasswordErrorHMM.Connection)
            )
        }
    }
})
