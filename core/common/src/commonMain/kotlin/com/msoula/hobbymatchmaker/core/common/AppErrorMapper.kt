package com.msoula.hobbymatchmaker.core.common

import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.FirestoreExceptionCode
import dev.gitlive.firebase.firestore.code
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
    else -> AppError.Network.Unknown(this)
}

fun Throwable.toStorageError(): AppError = when (this) {
    is CancellationException -> AppError.Network.Canceled
    is TimeoutCancellationException -> AppError.Network.Timeout

    is SerializationException -> AppError.Storage.Corrupted

    is IOException -> AppError.Storage.ReadFailed

    is IllegalArgumentException -> AppError.Domain.Validation(message ?: "Invalid argument")
    else -> AppError.Storage.ReadFailed
}

fun Throwable.toFirebaseError(): AppError = when (this) {
    is CancellationException -> AppError.Network.Canceled
    is TimeoutCancellationException -> AppError.Network.Timeout

    is FirebaseFirestoreException -> when (this.code) {
        FirestoreExceptionCode.CANCELLED -> AppError.Network.Canceled
        FirestoreExceptionCode.DEADLINE_EXCEEDED -> AppError.Network.Timeout
        FirestoreExceptionCode.UNAVAILABLE -> AppError.Network.Unreachable

        FirestoreExceptionCode.NOT_FOUND -> AppError.Domain.NotFound
        FirestoreExceptionCode.ALREADY_EXISTS -> AppError.Storage.WriteFailed
        FirestoreExceptionCode.PERMISSION_DENIED -> AppError.Domain.Forbidden
        FirestoreExceptionCode.UNAUTHENTICATED -> AppError.Domain.Unauthorized

        else -> AppError.Network.Unknown(this)
    }

    is IOException -> AppError.Network.Unreachable
    is IllegalArgumentException -> AppError.Domain.Validation(message ?: "Invalid argument")

    else -> AppError.Network.Unknown(this)
}

suspend inline fun <Entry> safeCall(crossinline block: suspend () -> Entry): R<Entry, AppError> =
    try {
        R.Success(block())
    } catch (t: Throwable) {
        if (t is CancellationException) throw t
        R.Failure(t.toAppError())
    }

suspend inline fun <Entry> safeFirebaseCall(crossinline block: suspend () -> Entry): R<Entry, AppError> =
    try {
        R.Success(block())
    } catch (t: Throwable) {
        if (t is CancellationException) throw t
        R.Failure(t.toFirebaseError())
    }

suspend inline fun <Entry> safeCallStorage(crossinline block: suspend () -> Entry): R<Entry, AppError> =
    try {
        R.Success(block())
    } catch (t: Throwable) {
        if (t is CancellationException) throw t
        R.Failure(t.toStorageError())
    }
