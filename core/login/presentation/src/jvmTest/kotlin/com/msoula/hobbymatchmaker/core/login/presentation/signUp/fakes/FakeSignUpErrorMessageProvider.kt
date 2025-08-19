package com.msoula.hobbymatchmaker.core.login.presentation.signUp.fakes

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpErrors
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.core.common.HMMAppError

class FakeSignUpErrorMessageProvider : ErrorMessageProvider {
    override suspend fun getMessage(error: HMMAppError): String {
        return when (error) {
            is SignUpErrors.UnknownErrorHMM -> "Unknown error oopsie"
            is SignUpErrors.EmailAlreadyExists -> "Address already exists"
            else -> ""
        }
    }
}
