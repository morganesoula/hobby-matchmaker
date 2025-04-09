package com.msoula.hobbymatchmaker.core.common

sealed class Result<out D, out E> {
    data class Success<out D>(val data: D) :
        Result<D, Nothing>()

    data class Failure(val error: AppError) :
        Result<Nothing, Nothing>()

    data object Loading : Result<Nothing, Nothing>()
}

interface AppError {
    val message: String
}

class ExternalServiceError(override val message: String = "External service error occurred") :
    AppError

suspend fun <Data, Out, Error> Result<Data, Error>.mapSuccess(
    transform: suspend (value: Data) -> Out
): Result<Out, Error> =
    when (this) {
        is Result.Success -> Result.Success(transform(this.data))
        is Result.Failure -> Result.Failure(this.error)
        is Result.Loading -> Result.Loading
    }

fun <Data, Error> Result<Data, Error>.mapError(
    transform: (value: AppError) -> AppError
): Result<Data, Error> =
    when (this) {
        is Result.Success -> Result.Success(this.data)
        is Result.Failure -> Result.Failure(transform(this.error))
        is Result.Loading -> Result.Loading
    }
