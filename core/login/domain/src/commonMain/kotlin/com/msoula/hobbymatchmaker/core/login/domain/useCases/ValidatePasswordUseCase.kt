package com.msoula.hobbymatchmaker.core.login.domain.useCases

import com.msoula.hobbymatchmaker.core.common.data.ValidationResult

class ValidatePasswordUseCase {
    operator fun invoke(password: String): ValidationResult {
        return if (password.matches(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*(),.?\":{}|<>])(?=\\S+\$).{8,}\$"
                    .toRegex()
            )
        ) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }
}
