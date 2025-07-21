package com.msoula.hobbymatchmaker.core.login.presentation.signIn.mappers

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInError
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.core.login.presentation.Res
import com.msoula.hobbymatchmaker.core.login.presentation.connection_issue
import com.msoula.hobbymatchmaker.core.login.presentation.login_error
import com.msoula.hobbymatchmaker.core.login.presentation.malformed_sign_in_error
import com.msoula.hobbymatchmaker.core.login.presentation.reset_password_error
import com.msoula.hobbymatchmaker.core.login.presentation.too_many_requests_error
import com.msoula.hobbymatchmaker.core.login.presentation.user_disabled_error
import com.msoula.hobbymatchmaker.core.login.presentation.user_not_found_error
import org.jetbrains.compose.resources.getString

class SignInErrorMessageProvider : ErrorMessageProvider {
    override suspend fun getMessage(error: AppError): String {
        return when (error) {
            is SignInError.WrongPassword -> getString(Res.string.login_error)
            is SignInError.UserNotFound -> getString(Res.string.user_not_found_error)
            is SignInError.UserDisabled -> getString(Res.string.user_disabled_error)
            is SignInError.TooManyRequests -> getString(Res.string.too_many_requests_error)
            is ResetPasswordError.Connection -> getString(Res.string.connection_issue)
            is ResetPasswordError.TooManyRequests -> getString(Res.string.too_many_requests_error)
            is ResetPasswordError.Other -> getString(Res.string.reset_password_error)
            else -> if (error.message.contains("incorrect"))
                getString(Res.string.malformed_sign_in_error) else error.message
        }
    }
}
