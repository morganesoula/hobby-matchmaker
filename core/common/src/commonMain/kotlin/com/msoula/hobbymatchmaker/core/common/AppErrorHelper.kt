package com.msoula.hobbymatchmaker.core.common

sealed class AppResult<out S, out E> {
    data class Success<S>(val data: S) : AppResult<S, Nothing>()
    data class Failure<E>(val error: E) : AppResult<Nothing, E>()
}

inline fun <S, E> AppResult<S, E>.onSuccess(block: (S) -> Unit): AppResult<S, E> {
    if (this is AppResult.Success) block(data)
    return this
}

inline fun <S, E> AppResult<S, E>.onFailure(block: (E) -> Unit): AppResult<S, E> {
    if (this is AppResult.Failure) block(this.error)
    return this
}

inline fun <Entry, Out, Error> AppResult<Entry, Error>.mapSuccess(transform: (Entry) -> Out): AppResult<Out, Error> =
    when (this) {
        is AppResult.Success -> AppResult.Success(transform(data))
        is AppResult.Failure -> this
    }

inline fun <Entry, Error, Out> AppResult<Entry, Error>.mapError(transform: (Error) -> Out): AppResult<Entry, Out> =
    when (this) {
        is AppResult.Success -> this
        is AppResult.Failure -> AppResult.Failure(transform(error))
    }

inline fun <Entry, Error, Output> AppResult<Entry, Error>.flatMap(transform: (Entry) -> AppResult<Output, Error>)
    : AppResult<Output, Error> =
    when (this) {
        is AppResult.Success -> transform(data)
        is AppResult.Failure -> this
    }

suspend inline fun <Entry, Error, Output> AppResult<Entry, Error>.flatMapSuspend(
    crossinline transform: suspend (Entry) -> AppResult<Output, Error>
): AppResult<Output, Error> =
    when (this) {
        is AppResult.Success -> transform(data)
        is AppResult.Failure -> this
    }
