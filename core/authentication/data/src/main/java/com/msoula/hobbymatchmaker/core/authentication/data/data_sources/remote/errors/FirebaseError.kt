package com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors

import com.msoula.hobbymatchmaker.core.common.AppError

sealed class CreateUserError : AppError {
    data object EmailAlreadyExists : CreateUserError()
    data object UserDisabled : CreateUserError()
    data object TooManyRequests : CreateUserError()
    data object InternalError : CreateUserError()
    data class Other(override val message: String) : CreateUserError()

    override val message: String
        get() = ""
}

sealed class SignInError : AppError {
    data object UserDisabled : SignInError()
    data object UserNotFound : SignInError()
    data object WrongPassword : SignInError()
    data object TooManyRequests : SignInError()
    data class Other(override val message: String) : SignInError()

    override val message: String
        get() = ""
}

class ResetPasswordError(override val message: String) : AppError
class FacebookError(override val message: String) : AppError
