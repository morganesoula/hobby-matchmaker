package com.msoula.hobbymatchmaker.core.common.validation

import com.msoula.hobbymatchmaker.core.common.data.ValidationResult

class ValidateEmailUseCase {
    operator fun invoke(email: String): ValidationResult {
        return if (email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
                .toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }
}
