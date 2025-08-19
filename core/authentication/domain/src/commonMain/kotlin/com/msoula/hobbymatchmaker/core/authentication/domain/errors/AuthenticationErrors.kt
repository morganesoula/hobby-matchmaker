package com.msoula.hobbymatchmaker.core.authentication.domain.errors

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInErrorHMM
import com.msoula.hobbymatchmaker.core.common.HMMAppError

sealed class SocialMediaErrorHMM(override val message: String) : HMMAppError {
    data class CredentialAlreadyInUse(val msg: String) : SocialMediaErrorHMM(msg)
    data class LinkSocialMediaErrorHMM(val msg: String) : SocialMediaErrorHMM(msg)
    data class AccountExistsWithDifferentCredential(val msg: String) : SocialMediaErrorHMM(msg)
    data class InvalidCredential(val msg: String) : SocialMediaErrorHMM(msg)
    data class UserDisabled(val msg: String = "") : SocialMediaErrorHMM(msg)
    data class OperationNotAllowed(val msg: String) : SocialMediaErrorHMM(msg)
    data class TooManyRequests(val msg: String = "") : SocialMediaErrorHMM(msg)
    data class Network(val msg: String) : SocialMediaErrorHMM(msg)
    data class Canceled(val msg: String = "") : SocialMediaErrorHMM(msg)
    data class Other(val msg: String) : SocialMediaErrorHMM(msg)
}

sealed class CreateUserWithEmailAndPasswordErrorHMM(override val message: String) : HMMAppError {
    data object EmailAlreadyExists : CreateUserWithEmailAndPasswordErrorHMM("")
    data object UserDisabled : CreateUserWithEmailAndPasswordErrorHMM("")
    data object TooManyRequests : CreateUserWithEmailAndPasswordErrorHMM("")
    data object InternalErrorHMM : CreateUserWithEmailAndPasswordErrorHMM("")
    data object Connection : CreateUserWithEmailAndPasswordErrorHMM("")
    data class Other(val msg: String) : CreateUserWithEmailAndPasswordErrorHMM(msg)
}

sealed class SignInWithEmailAndPasswordErrorHMM(override val message: String) : HMMAppError {
    data object UserDisabled : SignInWithEmailAndPasswordErrorHMM("")
    data object UserNotFound : SignInWithEmailAndPasswordErrorHMM("")
    data object WrongPassword : SignInWithEmailAndPasswordErrorHMM("")
    data object TooManyRequests : SignInWithEmailAndPasswordErrorHMM("")
    data object Connection : SignInWithEmailAndPasswordErrorHMM("")
    data class Other(val msg: String) : SignInWithEmailAndPasswordErrorHMM(msg)
}

sealed class ResetPasswordErrorHMM(override val message: String) : HMMAppError {
    data object TooManyRequests : ResetPasswordErrorHMM("")
    data object Other : ResetPasswordErrorHMM("")
    data object Connection : ResetPasswordErrorHMM("")
}

sealed class LogOutErrorHMM(override val message: String) : HMMAppError {
    data class FirebaseException(val firebaseErrorMessage: String) :
        LogOutErrorHMM(firebaseErrorMessage)
    data class UnknownErrorHMM(val unknownError: String) : LogOutErrorHMM(unknownError)
}

data class InvalidCredentialError(override val message: String) : RuntimeException(message)

fun HMMAppError.toSignInError(): SignInErrorHMM {
    val msg = message.takeIf { it.isNotBlank() } ?: "Error happened"

    return when (this) {
        is SignInWithEmailAndPasswordErrorHMM.UserNotFound -> SignInErrorHMM.UserNotFound
        is SignInWithEmailAndPasswordErrorHMM.WrongPassword -> SignInErrorHMM.WrongPassword
        is SignInWithEmailAndPasswordErrorHMM.UserDisabled -> SignInErrorHMM.UserDisabled
        is SignInWithEmailAndPasswordErrorHMM.TooManyRequests -> SignInErrorHMM.TooManyRequests

        is SocialMediaErrorHMM.CredentialAlreadyInUse,
        is SocialMediaErrorHMM.AccountExistsWithDifferentCredential ->
            SignInErrorHMM.AccountAlreadyExists(msg)

        is SocialMediaErrorHMM.UserDisabled -> SignInErrorHMM.UserDisabled

        is SocialMediaErrorHMM.InvalidCredential,
        is SocialMediaErrorHMM.OperationNotAllowed,
        is SocialMediaErrorHMM.TooManyRequests,
        is SocialMediaErrorHMM.Network,
        is SocialMediaErrorHMM.Canceled,
        is SocialMediaErrorHMM.LinkSocialMediaErrorHMM,
        is SocialMediaErrorHMM.Other -> SignInErrorHMM.Other(msg)

        else -> SignInErrorHMM.Other(msg)
    }
}
