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

class ResetPasswordError(override val message: String) : AppError
class LogOutError(override val message: String) : AppError
