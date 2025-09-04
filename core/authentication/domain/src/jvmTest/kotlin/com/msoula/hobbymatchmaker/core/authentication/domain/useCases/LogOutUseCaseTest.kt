package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.fakes.FakeAuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.fakes.FakeSessionRepository
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class LogOutUseCaseTest : StringSpec({

    val dispatcher = StandardTestDispatcher()
    val fakeSessionRepository = FakeSessionRepository()
    val fakeAuthenticationRepository = FakeAuthenticationRepository(fakeSessionRepository)
    val setIsConnectedUseCase = SetIsConnectedUseCase(fakeSessionRepository)
    val observeIsConnectedUseCase = ObserveIsConnectedUseCase(fakeSessionRepository)

    val useCase = LogOutUseCase(
        dispatcher,
        fakeAuthenticationRepository,
        setIsConnectedUseCase,
        observeIsConnectedUseCase
    )

    "should emit Success when log out is successful" {
        runTest(dispatcher) {
            fakeSessionRepository.isConnectedFlow.value = true
            val results = useCase.execute(Parameters.StringParam("true")).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Success(LogOutSuccess)
            )

            fakeSessionRepository.isConnectedFlow.value shouldBe false
        }
    }

    "should emit Failure.UnknownError when log out is NOT successful" {
        runTest(dispatcher) {
            fakeSessionRepository.isConnectedFlow.value = false
            val results = useCase.execute(Parameters.StringParam("false")).toList()

            advanceUntilIdle()

            results shouldBe listOf(
                Result.Loading,
                Result.Failure(LogOutErrorHMM.UnknownErrorHMM("weird error message"))
            )
        }
    }
})
