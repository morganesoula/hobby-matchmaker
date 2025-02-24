package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ResetPasswordUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val dispatcher: CoroutineDispatcher
) :
    FlowUseCase<Parameters.StringParam, ResetPasswordSuccess, ResetPasswordErrors>(dispatcher) {

    override fun execute(parameters: Parameters.StringParam):
        Flow<Result<ResetPasswordSuccess, ResetPasswordErrors>> {
        return flow {
            emit(Result.Loading)

            when (val result = authenticationRepository.resetPassword(parameters.value)) {
                is Result.Success -> emit(Result.Success(ResetPasswordSuccess))
                is Result.Failure -> emit(Result.Failure(result.error))
                else -> Unit
            }
        }.flowOn(dispatcher)
    }
}

data object ResetPasswordSuccess
data class ResetPasswordErrors(override val message: String) : AppError
