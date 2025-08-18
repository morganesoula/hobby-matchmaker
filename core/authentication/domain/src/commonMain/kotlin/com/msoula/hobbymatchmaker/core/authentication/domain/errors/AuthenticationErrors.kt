package com.msoula.hobbymatchmaker.core.authentication.domain.errors

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInError
import com.msoula.hobbymatchmaker.core.common.AppError

sealed class SocialMediaError(override val message: String) : AppError {
    data class CredentialAlreadyInUse(val msg: String) : SocialMediaError(msg)
    data class LinkSocialMediaError(val msg: String) : SocialMediaError(msg)
    data class AccountExistsWithDifferentCredential(val msg: String) : SocialMediaError(msg)
    data class InvalidCredential(val msg: String) : SocialMediaError(msg)
    data class UserDisabled(val msg: String = "") : SocialMediaError(msg)
    data class OperationNotAllowed(val msg: String) : SocialMediaError(msg)
    data class TooManyRequests(val msg: String = "") : SocialMediaError(msg)
    data class Network(val msg: String) : SocialMediaError(msg)
    data class Canceled(val msg: String = "") : SocialMediaError(msg)
    data class Other(val msg: String) : SocialMediaError(msg)
}

sealed class CreateUserWithEmailAndPasswordError(override val message: String) : AppError {
    data object EmailAlreadyExists : CreateUserWithEmailAndPasswordError("")
    data object UserDisabled : CreateUserWithEmailAndPasswordError("")
    data object TooManyRequests : CreateUserWithEmailAndPasswordError("")
    data object InternalError : CreateUserWithEmailAndPasswordError("")
    data object Connection : CreateUserWithEmailAndPasswordError("")
    data class Other(val msg: String) : CreateUserWithEmailAndPasswordError(msg)
}

sealed class SignInWithEmailAndPasswordError(override val message: String) : AppError {
    data object UserDisabled : SignInWithEmailAndPasswordError("")
    data object UserNotFound : SignInWithEmailAndPasswordError("")
    data object WrongPassword : SignInWithEmailAndPasswordError("")
    data object TooManyRequests : SignInWithEmailAndPasswordError("")
    data object Connection : SignInWithEmailAndPasswordError("")
    data class Other(val msg: String) : SignInWithEmailAndPasswordError(msg)
}

sealed class ResetPasswordError(override val message: String) : AppError {
    data object TooManyRequests : ResetPasswordError("")
    data object Other : ResetPasswordError("")
    data object Connection : ResetPasswordError("")
}

sealed class LogOutError(override val message: String) : AppError {
    data class FirebaseException(val firebaseErrorMessage: String) :
        LogOutError(firebaseErrorMessage)
    data class UnknownError(val unknownError: String) : LogOutError(unknownError)
}

data class InvalidCredentialError(override val message: String) : RuntimeException(message)

fun AppError.toSignInError(): SignInError {
    val msg = message.takeIf { it.isNotBlank() } ?: "Error happened"

    return when (this) {
        is SignInWithEmailAndPasswordError.UserNotFound -> SignInError.UserNotFound
        is SignInWithEmailAndPasswordError.WrongPassword -> SignInError.WrongPassword
        is SignInWithEmailAndPasswordError.UserDisabled -> SignInError.UserDisabled
        is SignInWithEmailAndPasswordError.TooManyRequests -> SignInError.TooManyRequests

        is SocialMediaError.CredentialAlreadyInUse,
        is SocialMediaError.AccountExistsWithDifferentCredential ->
            SignInError.AccountAlreadyExists(msg)

        is SocialMediaError.UserDisabled -> SignInError.UserDisabled

        is SocialMediaError.InvalidCredential,
        is SocialMediaError.OperationNotAllowed,
        is SocialMediaError.TooManyRequests,
        is SocialMediaError.Network,
        is SocialMediaError.Canceled,
        is SocialMediaError.LinkSocialMediaError,
        is SocialMediaError.Other -> SignInError.Other(msg)

        else -> SignInError.Other(msg)
    }
}
