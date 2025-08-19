package com.msoula.hobbymatchmaker.core.common

sealed class Result<out D, out E> {
    data class Success<out D>(val data: D) :
        Result<D, Nothing>()

    data class Failure<E>(val error: E) :
        Result<Nothing, E>()

    data object Loading : Result<Nothing, Nothing>()
}

interface HMMAppError {
    val message: String
}

sealed class NetworkErrorHMM(override val message: String) : HMMAppError {
    data class Connection(val reason: String) : NetworkErrorHMM(reason)
}

class ExternalServiceErrorHMM(override val message: String = "External service error occurred") :
    HMMAppError

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
