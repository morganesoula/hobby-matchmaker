package com.msoula.hobbymatchmaker.core.di.domain.useCase

import com.msoula.hobbymatchmaker.core.di.domain.ValidationResult

class ValidateNameUseCase {
    operator fun invoke(name: String): ValidationResult {
        return if (name.matches("^[a-zA-Z]+\$".toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }
}
