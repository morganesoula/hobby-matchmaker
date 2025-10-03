package com.msoula.hobbymatchmaker.core.di.domain.useCases

import com.msoula.hobbymatchmaker.core.di.data.ValidationResult

class ValidatePasswordUseCase {
    fun validateLoginPassword(password: String): ValidationResult {
        return ValidationResult(password.isNotEmpty())
    }
}
