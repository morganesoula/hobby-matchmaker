package com.msoula.hobbymatchmaker.core.common

sealed class R<out S, out E> {
    data class Success<S>(val data: S) : R<S, Nothing>()
    data class Failure<E>(val error: E) : R<Nothing, E>()
}

inline fun <S, E> R<S, E>.onSuccess(block: (S) -> Unit): R<S, E> {
    if (this is R.Success) block(data)
    return this
}

inline fun <S, E> R<S, E>.onFailure(block: (E) -> Unit): R<S, E> {
    if (this is R.Failure) block(error)
    return this
}

inline fun <Entry, Out, Error> R<Entry, Error>.map(transform: (Entry) -> Out): R<Out, Error> =
    when (this) {
        is R.Success -> R.Success(transform(data))
        is R.Failure -> this
    }

inline fun <Entry, Error, Out> R<Entry, Error>.mapError(transform: (Error) -> Out): R<Entry, Out> =
    when (this) {
        is R.Success -> this
        is R.Failure -> R.Failure(transform(error))
    }

inline fun <Entry, Error> R<Entry, Error>.flatMap(transform: (Entry) -> R<*, Error>): R<*, Error> =
    when (this) {
        is R.Success -> transform(data)
        is R.Failure -> this
    }
