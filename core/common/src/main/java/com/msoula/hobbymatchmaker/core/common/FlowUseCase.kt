package com.msoula.hobbymatchmaker.core.common

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

sealed class Parameters {
    data class LongParam(val value: Long) : Parameters()
    data class StringParam(val value: String) : Parameters()
    data class LongStringParam(val longValue: Long, val stringValue: String) : Parameters()
}

abstract class FlowUseCase<in Parameters, Success, BusinessRuleError>(private val dispatcher: CoroutineDispatcher) {

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    operator fun invoke(parameters: Parameters): Flow<Result<Success, BusinessRuleError>> {
        return execute(parameters)
            .catch { e ->
                Log.e("HMM", "An error occurred while executing the use case", e)
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
