package com.msoula.hobbymatchmaker.core.login.presentation.signUp.fakes

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpErrors
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider

class FakeSignUpErrorMessageProvider : ErrorMessageProvider {
    override suspend fun getMessage(error: AppError): String {
        return when (error) {
            is SignUpErrors.UnknownError -> "Unknown error oopsie"
            is SignUpErrors.EmailAlreadyExists -> "Adresse e-mail déjà utilisée"
            else -> ""
        }
    }
}
