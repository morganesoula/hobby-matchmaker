package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LogOutUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val authenticationRepository: AuthenticationRepository,
    private val setIsConnectedUseCase: SetIsConnectedUseCase,
    private val observeIsConnectedUseCase: ObserveIsConnectedUseCase
) : FlowUseCase<Parameters, LogOutSuccess, LogOutError>(dispatcher) {

    override fun execute(parameters: Parameters): Flow<Result<LogOutSuccess, LogOutError>> {
        return flow {
            emit(Result.Loading)

            when (val result = authenticationRepository.logOut()) {
                is Result.Success -> {
                    setIsConnectedUseCase(false)

                    observeIsConnectedUseCase()
                        .first { it == false }

                    emit(Result.Success(LogOutSuccess))
                }

                is Result.Failure -> emit(Result.Failure(result.error))
                else -> Unit
            }
        }.flowOn(dispatcher)
    }
}

data object LogOutSuccess
