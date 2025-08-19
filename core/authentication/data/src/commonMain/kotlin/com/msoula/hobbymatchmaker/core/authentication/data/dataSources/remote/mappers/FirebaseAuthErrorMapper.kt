package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.common.HMMAppError
import com.msoula.hobbymatchmaker.core.common.Result
import kotlin.coroutines.cancellation.CancellationException

object FirebaseAuthErrorMapper {

    fun mapCreateUserError(message: String?): CreateUserWithEmailAndPasswordErrorHMM {
        val msg = message?.trim()?.lowercase()
            ?: return CreateUserWithEmailAndPasswordErrorHMM.Other("Unknown error")

        return when {
            msg.contains("email address is already in use", ignoreCase = true) ||
                msg.contains("already in use", ignoreCase = true) -> {
                CreateUserWithEmailAndPasswordErrorHMM.EmailAlreadyExists
            }

            msg.contains("user account has been disabled", ignoreCase = true) ->
                CreateUserWithEmailAndPasswordErrorHMM.UserDisabled

            msg.contains("we have blocked all requests", ignoreCase = true) ->
                CreateUserWithEmailAndPasswordErrorHMM.TooManyRequests

            msg.contains("internal error", ignoreCase = true) ||
                msg.contains("internal_error", ignoreCase = true) ->
                CreateUserWithEmailAndPasswordErrorHMM.InternalErrorHMM

            msg.contains("network error", ignoreCase = true) ->
                CreateUserWithEmailAndPasswordErrorHMM.Connection

            else -> CreateUserWithEmailAndPasswordErrorHMM.Other(message)
        }
    }

    fun mapSignInError(message: String?): SignInWithEmailAndPasswordErrorHMM {
        val msg = message?.trim()?.lowercase()
            ?: return SignInWithEmailAndPasswordErrorHMM.Other("Unknown error")

        return when {
            msg.contains("password is invalid", ignoreCase = true) ||
                msg.contains("ERROR_WRONG_PASSWORD", ignoreCase = true) ->
                SignInWithEmailAndPasswordErrorHMM.WrongPassword

            msg.contains("no user record", ignoreCase = true) ||
                msg.contains("ERROR_USER_NOT_FOUND", ignoreCase = true) ->
                SignInWithEmailAndPasswordErrorHMM.UserNotFound

            msg.contains("user account has been disabled", ignoreCase = true) ||
                msg.contains("ERROR_USER_DISABLED", ignoreCase = true) ->
                SignInWithEmailAndPasswordErrorHMM.UserDisabled

            msg.contains("ERROR_TOO_MANY_REQUESTS", ignoreCase = true) ->
                SignInWithEmailAndPasswordErrorHMM.TooManyRequests

            msg.contains("network error", ignoreCase = true) ->
                SignInWithEmailAndPasswordErrorHMM.Connection

            else -> SignInWithEmailAndPasswordErrorHMM.Other(message)
        }
    }

    fun mapResetPasswordError(message: String?): ResetPasswordErrorHMM {
        val msg = message?.trim()?.lowercase()
            ?: return ResetPasswordErrorHMM.Other

        return when {
            "too many requests" in msg -> ResetPasswordErrorHMM.TooManyRequests

            msg.contains("network error", ignoreCase = true) ->
                ResetPasswordErrorHMM.Connection

            else -> ResetPasswordErrorHMM.Other
        }
    }
}

suspend inline fun <T, reified E : HMMAppError> safeCallTyped(
    crossinline block: suspend () -> Result<T, E>
): Result<T, E> {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.Failure(
            when (E::class) {
                SignInWithEmailAndPasswordErrorHMM::class ->
                    FirebaseAuthErrorMapper.mapSignInError(e.message) as E

                ResetPasswordErrorHMM::class ->
                    FirebaseAuthErrorMapper.mapResetPasswordError(e.message) as E

                CreateUserWithEmailAndPasswordErrorHMM::class ->
                    FirebaseAuthErrorMapper.mapCreateUserError(e.message) as E

                else -> throw IllegalStateException("Unhandled error type: ${E::class.simpleName}")
            }
        )
    }
}

