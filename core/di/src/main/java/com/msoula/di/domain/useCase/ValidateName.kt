package com.msoula.di.domain.useCase

import com.msoula.di.domain.ValidationResult

class ValidateName {
    operator fun invoke(name: String): ValidationResult {
        return if (name.matches("^[a-zA-Z]+\$".toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }
}
