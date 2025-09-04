package com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInErrorHMM
import com.msoula.hobbymatchmaker.core.common.HMMAppError

class FakeSignInErrorMessageProvider : ErrorMessageProvider {
    override suspend fun getMessage(error: HMMAppError): String {
        return when (error) {
            is SignInErrorHMM.WrongPassword -> "wrong password error"
            is SignInErrorHMM.UserNotFound -> "user not found"
            is SignInErrorHMM.Other -> "Simulated sign-in error"
            is ResetPasswordErrorHMM.Other -> "unknown error while resetting"
            else -> "random error"
        }
    }
}
