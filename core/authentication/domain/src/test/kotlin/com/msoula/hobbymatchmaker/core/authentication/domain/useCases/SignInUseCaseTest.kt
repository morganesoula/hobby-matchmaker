package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.fakes.FakeAuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.fakes.FakeSessionRepository
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test


class SignInUseCaseTest {
    private val testDispatcher = StandardTestDispatcher()
    private val fakeAuthenticationRepository = FakeAuthenticationRepository()
    private val fakeSessionRepository = FakeSessionRepository()

    @Test
    fun `when signIn with email & password are not empty, return success`() =
        runTest(testDispatcher) {
            val results = setUp("testEmail", "testPassword")

            assert(results.size == 2)
            assert(results[0] is Result.Loading)
            assert(results[1] is Result.Success)
            assert((results[1] as Result.Success).data == SignInSuccess)
        }

    @Test
    fun `when signIn with email empty, return error`() = runTest(testDispatcher) {
        val results = setUp("","testPassword")

        assert(results.size == 2)
        assert(results[0] is Result.Loading)
        assert(results[1] is Result.Failure)
        assert((results[1] as Result.Failure).error == SignInError.Other)
    }

    @Test
    fun `when signIn with empty password, return WrongPasswordError`() = runTest(testDispatcher) {
        val results = setUp("testEmail", "")

        println("Results are: $results")
        assert(results.size == 2)
        assert(results[0] is Result.Loading)
        assert(results[1] is Result.Failure)
        assert((results[1] as Result.Failure).error == SignInError.WrongPassword)
    }

    private suspend fun setUp(
        email: String,
        password: String
    ): MutableList<Result<SignInSuccess, SignInError>> {
        val signInUseCase = SignInUseCase(
            dispatcher = testDispatcher,
            authenticationRepository = fakeAuthenticationRepository,
            setIsConnectedUseCase = SetIsConnectedUseCase(fakeSessionRepository)
        )

        val results = mutableListOf<Result<SignInSuccess, SignInError>>()
        signInUseCase(Parameters.DoubleStringParam(email, password)).collect {
            results.add(it)
        }

        return results
    }
}
