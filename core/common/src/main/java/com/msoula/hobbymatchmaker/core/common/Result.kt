package com.msoula.hobbymatchmaker.core.common

sealed interface Result<out R> {
    data class Success<out R>(val data: R) :
        Result<R>

    data class Failure(val error: AppError) :
        Result<Nothing>
}

interface AppError {
    val message: String
}

class NetworkError(override val message: String = "No internet connection") : AppError
class ServerError(override val message: String = "Server error occurred") : AppError
class ExternalServiceError(override val message: String = "External service error occurred") :
    AppError

suspend fun <R, S> Result<R>.mapSuccess(transform: suspend (value: R) -> S): Result<S> =
    when (this) {
        is Result.Success -> Result.Success(transform(this.data))
        is Result.Failure -> Result.Failure(this.error)
    }

fun <R> Result<R>.mapError(transform: (value: AppError) -> AppError): Result<R> =
    when (this) {
        is Result.Failure -> Result.Failure(transform(this.error))
        is Result.Success -> Result.Success(this.data)
    }

fun <T> Result<T>.onEach(action: (Result<T>) -> Unit): Result<T> {
    action(this@onEach)
    return this
}

