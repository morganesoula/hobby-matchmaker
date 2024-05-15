package com.msoula.hobbymatchmaker.core.login.domain.use_cases

import com.msoula.hobbymatchmaker.core.common.ValidationResult

class ValidateNameUseCase {
    operator fun invoke(name: String): ValidationResult {
        return if (name.matches("^[a-zA-Z]+\$".toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }
}
