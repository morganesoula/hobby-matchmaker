package com.msoula.hobbymatchmaker.core.common

sealed class Result<out D, out E> {
    data class Success<out D>(val data: D) :
        Result<D, Nothing>()

    data class Failure<E>(val error: E) :
        Result<Nothing, E>()

    data object Loading : Result<Nothing, Nothing>()
}

interface AppError {
    val message: String
}

sealed class NetworkError(override val message: String) : AppError {
    data class Connection(val reason: String) : NetworkError(reason)
}

class ExternalServiceError(override val message: String = "External service error occurred") :
    AppError

suspend fun <Data, Out, Error> Result<Data, Error>.mapSuccess(
    transform: suspend (value: Data) -> Out
): Result<Out, Error> = when (this) {
    is Result.Success -> Result.Success(transform(this.data))
    is Result.Failure -> Result.Failure(this.error)
    is Result.Loading -> Result.Loading
}

fun <Data, Error, NE> Result<Data, Error>.mapError(
    transform: (value: Error) -> NE
): Result<Data, NE> = when (this) {
    is Result.Success -> Result.Success(this.data)
    is Result.Failure -> Result.Failure(transform(this.error))
    is Result.Loading -> Result.Loading
}
