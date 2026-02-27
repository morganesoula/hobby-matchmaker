package com.msoula.hobbymatchmaker.core.common

import dev.gitlive.firebase.FirebaseApiNotAvailableException
import dev.gitlive.firebase.FirebaseException
import dev.gitlive.firebase.FirebaseNetworkException
import dev.gitlive.firebase.FirebaseTooManyRequestsException
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.FirestoreExceptionCode
import dev.gitlive.firebase.firestore.code
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

fun Throwable.toGenericAppError(): AppError = when (this) {
    is CancellationException -> AppError.Network.Canceled
    is TimeoutCancellationException -> AppError.Network.Timeout
    is SerializationException -> AppError.Network.Serialization
    is ClientRequestException -> AppError.Network.Http(response.status.value, message)
    is ServerResponseException -> AppError.Network.Http(response.status.value, message)

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
    is IllegalStateException -> AppError.Authentication.Unknown

    is FirebaseNetworkException -> AppError.Network.Unreachable
    is FirebaseTooManyRequestsException -> AppError.Network.Http(429, message)
    is FirebaseApiNotAvailableException -> AppError.External.Service(
        provider = "Google Play Services / Platform",
        message
    )

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

    is FirebaseException -> {
        val m = message.orEmpty()
        when {
            RX_ALREADY_EXISTS.containsMatchIn(m) -> AppError.Authentication.AlreadyExists
            RX_WRONG_PASSWORD.containsMatchIn(m) -> AppError.Domain.Unauthorized
            RX_USER_NOT_FOUND.containsMatchIn(m) -> AppError.Domain.Unauthorized
            RX_USER_DISABLED.containsMatchIn(m) -> AppError.Domain.Forbidden
            RX_RECENT_REQUIRED.containsMatchIn(m) -> AppError.Domain.Unauthorized
            RX_INVALID_CRED.containsMatchIn(m) -> AppError.Authentication.InvalidCredentials
            RX_WEAK_PASSWORD.containsMatchIn(m) -> AppError.Domain.Validation("Weak password")
            else -> AppError.Authentication.Unknown
        }
    }

    is IOException -> AppError.Network.Unreachable
    is IllegalArgumentException -> AppError.Domain.Validation(message ?: "Invalid argument")

    else -> AppError.Network.Unknown(this)
}

private val RX_ALREADY_EXISTS =
    Regex("already in use|email.*exists|ERROR_EMAIL_ALREADY_IN_USE", RegexOption.IGNORE_CASE)
private val RX_WRONG_PASSWORD = Regex(
    "wrong password|invalid password|INVALID_PASSWORD|ERROR_WRONG_PASSWORD",
    RegexOption.IGNORE_CASE
)
private val RX_USER_NOT_FOUND =
    Regex("no user record|user.*not.*found|ERROR_USER_NOT_FOUND", RegexOption.IGNORE_CASE)
private val RX_USER_DISABLED = Regex("user.*disabled|ERROR_USER_DISABLED", RegexOption.IGNORE_CASE)
private val RX_RECENT_REQUIRED =
    Regex("recent login|REQUIRES_RECENT_LOGIN", RegexOption.IGNORE_CASE)
private val RX_INVALID_CRED =
    Regex("invalid credential|INVALID_CREDENTIAL|invalid email", RegexOption.IGNORE_CASE)
private val RX_WEAK_PASSWORD = Regex("weak password|WEAK_PASSWORD", RegexOption.IGNORE_CASE)

suspend inline fun <Entry> safeCall(crossinline block: suspend () -> Entry): AppResult<Entry, AppError> =
    try {
        AppResult.Success(block())
    } catch (t: Throwable) {
        if (t is CancellationException) throw t
        AppResult.Failure(t.toGenericAppError())
    }

suspend inline fun <Entry> safeFirebaseCall(crossinline block: suspend () -> Entry): AppResult<Entry, AppError> =
    try {
        AppResult.Success(block())
    } catch (t: Throwable) {
        if (t is CancellationException) throw t
        AppResult.Failure(t.toFirebaseError())
    }

suspend inline fun <Entry> safeCallStorage(crossinline block: suspend () -> Entry): AppResult<Entry, AppError> =
    try {
        AppResult.Success(block())
    } catch (t: Throwable) {
        if (t is CancellationException) throw t
        AppResult.Failure(t.toStorageError())
    }

inline fun <Out : Any, Error> AppResult<Out?, Error>.requireNonNull(onNull: () -> Error): AppResult<Out, Error> =
    when (this) {
        is AppResult.Success -> data?.let { AppResult.Success(it) } ?: AppResult.Failure(onNull())
        is AppResult.Failure -> this
    }
