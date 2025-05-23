package com.msoula.hobbymatchmaker.core.login.domain.useCases

import com.msoula.hobbymatchmaker.core.common.data.ValidationResult

class ValidateNameUseCase {
    operator fun invoke(name: String): ValidationResult {
        return if (name.matches("^[a-zA-Z-]+\$".toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }
}
