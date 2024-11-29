package com.msoula.hobbymatchmaker.testUtils.core.authentication.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import com.msoula.hobbymatchmaker.testUtils.common.fakes.FakeAuthenticationRepository
import com.msoula.hobbymatchmaker.testUtils.common.fakes.FakeSessionRepository
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
        val results = setUp(false)

        assert(results.size == 2)
        assert(results[0] is Result.Loading)
        assert(results[1] is Result.Failure)
        assert((results[1] as Result.Failure).error is LogOutError.UnknownError)
    }

    @Test
    fun `should be logged out when log out succeeds`() = runTest(testDispatcher) {
        val results = setUp(true)

        assert(results.size == 1)
        assert(results[0] is Result.Loading)
        assert(!fakeSessionRepository.observeIsConnected().first())
    }

    private suspend fun setUp(logOut: Boolean): MutableList<Result<LogOutSuccess, LogOutError>> {
        val logOutUseCase = LogOutUseCase(
            dispatcher = testDispatcher,
            authenticationRepository = fakeAuthenticationRepository,
            setIsConnectedUseCase = SetIsConnectedUseCase(fakeSessionRepository)
        )

        fakeAuthenticationRepository.shouldLogOut(logOut)

        val results = mutableListOf<Result<LogOutSuccess, LogOutError>>()
        logOutUseCase(Parameters.StringParam("")).collect {
            results.add(it)
        }

        return results
    }
}
