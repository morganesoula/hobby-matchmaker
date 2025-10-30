package com.msoula.hobbymatchmaker.core.common.validation

import com.msoula.hobbymatchmaker.core.common.data.ValidationResult

class ValidatePasswordUseCase {
    fun validateLoginPassword(password: String): ValidationResult {
        return ValidationResult(password.isNotEmpty())
    }
}
