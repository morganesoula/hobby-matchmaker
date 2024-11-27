package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.fakes.FakeAuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ResetPasswordUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun `when reset password is successful, return success`() = runTest(testDispatcher) {
        val results = setUp("testEmail")

        assert(results.size == 2)
        assert(results[0] is Result.Loading)
        assert(results[1] is Result.Success)
        assert((results[1] as Result.Success).data is ResetPasswordSuccess)
    }

    @Test
    fun `when email is empty, return failure`() = runTest(testDispatcher) {
        val results = setUp("")
        assert(results.size == 2)
        assert(results[0] is Result.Loading)
        assert(results[1] is Result.Failure)
        assert((results[1] as Result.Failure).error is ResetPasswordError)
    }

    private suspend fun setUp(email: String): MutableList<Result<ResetPasswordSuccess, ResetPasswordErrors>> {
        val fakeAuthenticationRepository = FakeAuthenticationRepository()
        val resetPasswordUseCase = ResetPasswordUseCase(
            authenticationRepository = fakeAuthenticationRepository,
            dispatcher = testDispatcher
        )

        val results = mutableListOf<Result<ResetPasswordSuccess, ResetPasswordErrors>>()
        resetPasswordUseCase(Parameters.StringParam(email)).collect {
            results.add(it)
        }

        return results
    }
}
