package com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInError
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider

class FakeSignInErrorMessageProvider : ErrorMessageProvider {
    override suspend fun getMessage(error: AppError): String {
        return when (error) {
            is SignInError.WrongPassword -> "wrong password error"
            else -> "random error"
        }
    }
}
