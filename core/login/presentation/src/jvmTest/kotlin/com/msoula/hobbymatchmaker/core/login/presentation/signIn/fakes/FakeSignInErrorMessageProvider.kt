package com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInError
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider

class FakeSignInErrorMessageProvider : ErrorMessageProvider {
    override suspend fun getMessage(error: AppError): String {
        return when (error) {
            is SignInError.WrongPassword -> "wrong password error"
            is SignInError.Other -> "Simulated sign-in error"
            is ResetPasswordError.Other -> "unknown error while resetting"
            else -> "random error"
        }
    }
}
