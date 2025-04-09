package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Result
import kotlin.coroutines.cancellation.CancellationException

object FirebaseAuthErrorMapper {

    fun mapCreateUserError(message: String?): CreateUserWithEmailAndPasswordError {
        val msg = message?.trim()?.lowercase()
            ?: return CreateUserWithEmailAndPasswordError.Other("Unknown error")

        return when {
            msg.contains("email address is already in use", ignoreCase = true) ||
                msg.contains("already in use", ignoreCase = true) -> {
                CreateUserWithEmailAndPasswordError.EmailAlreadyExists
            }

            msg.contains("user account has been disabled", ignoreCase = true) ->
                CreateUserWithEmailAndPasswordError.UserDisabled

            msg.contains("we have blocked all requests", ignoreCase = true) ->
                CreateUserWithEmailAndPasswordError.TooManyRequests

            msg.contains("internal error", ignoreCase = true) ||
                msg.contains("internal_error", ignoreCase = true) ->
                CreateUserWithEmailAndPasswordError.InternalError

            msg.contains("network error", ignoreCase = true) ->
                CreateUserWithEmailAndPasswordError.Connection

            else -> CreateUserWithEmailAndPasswordError.Other(message)
        }
    }

    fun mapSignInError(message: String?): SignInWithEmailAndPasswordError {
        val msg = message?.trim()?.lowercase()
            ?: return SignInWithEmailAndPasswordError.Other("Unknown error")

        return when {
            msg.contains("password is invalid", ignoreCase = true) ||
                msg.contains("ERROR_WRONG_PASSWORD", ignoreCase = true) ->
                SignInWithEmailAndPasswordError.WrongPassword

            msg.contains("no user record", ignoreCase = true) ||
                msg.contains("ERROR_USER_NOT_FOUND", ignoreCase = true) ->
                SignInWithEmailAndPasswordError.UserNotFound

            msg.contains("user account has been disabled", ignoreCase = true) ||
                msg.contains("ERROR_USER_DISABLED", ignoreCase = true) ->
                SignInWithEmailAndPasswordError.UserDisabled

            msg.contains("ERROR_TOO_MANY_REQUESTS", ignoreCase = true) ->
                SignInWithEmailAndPasswordError.TooManyRequests

            msg.contains("network error", ignoreCase = true) ->
                SignInWithEmailAndPasswordError.Connection

            else -> SignInWithEmailAndPasswordError.Other(message)
        }
    }

    fun mapResetPasswordError(message: String?): ResetPasswordError {
        val msg = message?.trim()?.lowercase()
            ?: return ResetPasswordError.Other

        return when {
            "too many requests" in msg -> ResetPasswordError.TooManyRequests

            msg.contains("network error", ignoreCase = true) ->
                ResetPasswordError.Connection

            else -> ResetPasswordError.Other
        }
    }
}

suspend inline fun <T, reified E : AppError> safeCallTyped(
    crossinline block: suspend () -> Result<T, E>
): Result<T, E> {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.Failure(
            when (E::class) {
                SignInWithEmailAndPasswordError::class ->
                    FirebaseAuthErrorMapper.mapSignInError(e.message) as E

                ResetPasswordError::class ->
                    FirebaseAuthErrorMapper.mapResetPasswordError(e.message) as E

                CreateUserWithEmailAndPasswordError::class ->
                    FirebaseAuthErrorMapper.mapCreateUserError(e.message) as E

                else -> throw IllegalStateException("Unhandled error type: ${E::class.simpleName}")
            }
        )
    }
}

