package com.msoula.di.domain.useCase

import com.msoula.di.domain.ValidationResult

class ValidateEmail {
    operator fun invoke(email: String): ValidationResult {
        return if (email.matches("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})$".toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }
}
