package com.msoula.hobbymatchmaker.core.login.presentation.interactors

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.data.ValidationResult
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase

class SignUpInteractor(
    private val validation: LoginValidateFormUseCase,
    private val signUpUseCase: SignUpUseCase
) {
    fun validateCredentials(email: String, password: String): Boolean =
        validation.validateEmail(email).successful &&
            validation.validatePassword(password).successful

    fun validateFirstName(firstName: String): ValidationResult =
        validation.validateFirstName(firstName)

    suspend fun createAccount(email: String, password: String): AppResult<Unit, AppError> =
        signUpUseCase(Parameters.DoubleStringParam(email, password))
            .mapSuccess { Unit }
}
