package com.msoula.hobbymatchmaker.core.common

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

fun Throwable.toAppError(): AppError = when (this) {
    is CancellationException -> AppError.Network.Canceled
    is TimeoutCancellationException -> AppError.Network.Timeout
    is SerializationException -> AppError.Network.Serialization
    is NoTransformationFoundException -> AppError.Network.Serialization

    is ResponseException -> {
        val code = response.status.value
        AppError.Network.Http(code, message)
    }

    is HttpRequestTimeoutException -> AppError.Network.Timeout

    is IOException -> AppError.Network.Unreachable

    is IllegalArgumentException -> AppError.Domain.Validation(message ?: "Invalid argument")
    else -> AppError.Network.Unknown(message)
}

fun Throwable.toStorageError(): AppError = when (this) {
    is CancellationException -> AppError.Network.Canceled
    is TimeoutCancellationException -> AppError.Network.Timeout

    is SerializationException -> AppError.Storage.Corrupted

    is IOException -> AppError.Storage.ReadFailed

    is IllegalArgumentException -> AppError.Domain.Validation(message ?: "Invalid argument")
    else -> AppError.Storage.ReadFailed
}

suspend inline fun <S> safeCall(crossinline block: () -> S): R<S, AppError> =
    try {
        R.Success(block())
    } catch (t: Throwable) {
        if (t is CancellationException) throw t
        R.Failure(t.toAppError())
    }

suspend inline fun <Entry> safeCallStorage(crossinline block: suspend () -> Entry): R<Entry, AppError> =
    try {
        R.Success(block())
    } catch (t: Throwable) {
        if (t is CancellationException) throw t
        R.Failure(t.toStorageError())
    }


