package com.msoula.hobbymatchmaker.testUtils.core.authentication.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpErrors
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import com.msoula.hobbymatchmaker.testUtils.common.fakes.FakeAuthenticationRepository
import com.msoula.hobbymatchmaker.testUtils.common.fakes.FakeSessionRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SignUpUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun `when signUp fails, returns according failure`() = runTest(testDispatcher) {
        val results = setUp("", "")

        assert(results.size == 2)
        assert(results[0] is Result.Loading)
        assert(results[1] is Result.Failure)
        assert((results[1] as Result.Failure).error is SignUpErrors.InternalError)

        val results2 = setUp("randomEmail", "")

        assert(results2.size == 2)
        assert(results2[0] is Result.Loading)
        assert(results2[1] is Result.Failure)
        assert((results2[1] as Result.Failure).error is SignUpErrors.EmailAlreadyExists)
    }

    @Test
    fun `when signUp succeeds and user is created, returns success`() = runTest(testDispatcher) {
        val results = setUp("randomEmail", "randomPassword")

        assert(results.size == 2)
        assert(results[0] is Result.Loading)
        assert(results[1] is Result.Success)
        assert((results[1] as Result.Success).data == SignUpSuccess(""))
    }

    @Test
    fun `when signUp succeeds and user is not created, returns error`() = runTest(testDispatcher) {
        val results = setUp("", "randomPassword")

        assert(results.size == 2)
        assert(results[0] is Result.Loading)
        assert(results[1] is Result.Failure)
        assert((results[1] as Result.Failure).error is SignUpErrors.CreateUserError.SaveError)
    }

    private suspend fun setUp(
        email: String,
        password: String
    ): MutableList<Result<SignUpSuccess, SignUpErrors>> {
        val fakeAuthenticationRepository = FakeAuthenticationRepository()
        val fakeSessionRepository = FakeSessionRepository()

        val signUpUseCase = SignUpUseCase(
            authenticationRepository = fakeAuthenticationRepository,
            createUserUseCase = CreateUserUseCase(fakeSessionRepository),
            dispatcher = testDispatcher
        )

        val results = mutableListOf<Result<SignUpSuccess, SignUpErrors>>()
        signUpUseCase(Parameters.DoubleStringParam(email, password)).collect {
            results.add(it)
        }

        return results
    }
}
