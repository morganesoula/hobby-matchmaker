package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.fakes.FakeAuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.fakes.FakeSessionRepository
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LogOutUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeAuthenticationRepository = FakeAuthenticationRepository()
    private val fakeSessionRepository = FakeSessionRepository()

    @Test
    fun `should return failure when log out does not succeed`() = runTest(testDispatcher) {
        val logOutUseCase = LogOutUseCase(
            dispatcher = testDispatcher,
            authenticationRepository = fakeAuthenticationRepository,
            setIsConnectedUseCase = SetIsConnectedUseCase(fakeSessionRepository)
        )

        fakeAuthenticationRepository.shouldLogOut(false)

        val results = mutableListOf<Result<LogOutSuccess, LogOutError>>()
        logOutUseCase(Parameters.StringParam("")).collect {
            results.add(it)
        }

        assert(results.size == 2)
        assert(results[0] is Result.Loading)
        assert(results[1] is Result.Failure)
        assert((results[1] as Result.Failure).error is LogOutError.UnknownError)
    }

    @Test
    fun `should be logged out when log out succeeds`() = runTest(testDispatcher) {
        val logOutUseCase = LogOutUseCase(
            dispatcher = testDispatcher,
            authenticationRepository = fakeAuthenticationRepository,
            setIsConnectedUseCase = SetIsConnectedUseCase(fakeSessionRepository)
        )

        fakeAuthenticationRepository.shouldLogOut(true)

        val results = mutableListOf<Result<LogOutSuccess, LogOutError>>()
        logOutUseCase(Parameters.StringParam("")).collect {
            results.add(it)
        }

        println("Results: $results")

        assert(results.size == 1)
        assert(results[0] is Result.Loading)
        assert(!fakeSessionRepository.observeIsConnected().first())
    }
}
