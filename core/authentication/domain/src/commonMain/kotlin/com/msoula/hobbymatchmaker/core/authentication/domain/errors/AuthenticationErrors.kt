package com.msoula.hobbymatchmaker.core.authentication.domain.errors

import com.msoula.hobbymatchmaker.core.common.AppError

sealed class SocialMediaError : AppError {
    data object SignInWithCredentialsError : SocialMediaError()
    data object LinkWithCredentialsError : SocialMediaError()

    override val message: String
        get() = ""
}

sealed class CreateUserWithEmailAndPasswordError : AppError {
    data object EmailAlreadyExists : CreateUserWithEmailAndPasswordError()
    data object UserDisabled : CreateUserWithEmailAndPasswordError()
    data object TooManyRequests : CreateUserWithEmailAndPasswordError()
    data object InternalError : CreateUserWithEmailAndPasswordError()
    data class Other(override val message: String) : CreateUserWithEmailAndPasswordError()

    override val message: String
        get() = ""
}

sealed class SignInWithEmailAndPasswordError : AppError {
    data object UserDisabled : SignInWithEmailAndPasswordError()
    data object UserNotFound : SignInWithEmailAndPasswordError()
    data object WrongPassword : SignInWithEmailAndPasswordError()
    data object TooManyRequests : SignInWithEmailAndPasswordError()
    data class Other(override val message: String) : SignInWithEmailAndPasswordError()

    override val message: String
        get() = ""
}

data class ResetPasswordError(override val message: String) : AppError
sealed class LogOutError(override val message: String) : AppError {
    data class FirebaseException(val firebaseErrorMessage: String) :
        LogOutError(firebaseErrorMessage)

    data class CredentialException(val credentialErrorMessage: String) :
        LogOutError(credentialErrorMessage)

    data class FacebookLogOutException(val facebookErrorMessage: String) :
        LogOutError(facebookErrorMessage)

    data class UnknownError(val unknownError: String) : LogOutError(unknownError)
}

data class GetGoogleCredentialError(override val message: String) : AppError
data class GetFacebookCredentialError(override val message: String) : AppError
data class GetFacebookClientError(override val message: String) : AppError
data class GetAppleCredentialError(override val message: String) : AppError

data class InvalidCredentialError(override val message: String) : RuntimeException(message)
data class GoogleCredentialNullError(override val message: String) : Exception(message)
