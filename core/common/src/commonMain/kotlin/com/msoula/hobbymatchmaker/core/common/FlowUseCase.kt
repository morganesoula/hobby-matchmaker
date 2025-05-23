package com.msoula.hobbymatchmaker.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

sealed class Parameters {
    data class StringParam(val value: String) : Parameters()
    data class DoubleStringParam(val firstValue: String, val secondValue: String) : Parameters()
    data class LongStringParam(val longValue: Long, val stringValue: String) : Parameters()
}

abstract class FlowUseCase<in Parameters, Success, BusinessRuleError>(private val dispatcher: CoroutineDispatcher) {

    operator fun invoke(parameters: Parameters): Flow<Result<Success, BusinessRuleError>> {
        return execute(parameters)
            .catch { e ->
                emit(
                    Result.Failure(
                        FlowUseCaseError(
                            e.message ?: "An error occurred while executing the use case"
                        )
                    )
                )
            }
            .flowOn(dispatcher)
    }

    abstract fun execute(parameters: Parameters): Flow<Result<Success, BusinessRuleError>>
}

class FlowUseCaseError(override val message: String) : AppError
