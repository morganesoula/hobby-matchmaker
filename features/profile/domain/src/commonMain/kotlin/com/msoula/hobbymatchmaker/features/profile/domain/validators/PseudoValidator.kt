package com.msoula.hobbymatchmaker.features.profile.domain.validators

import com.msoula.hobbymatchmaker.features.profile.domain.models.PseudoValidationError

object PseudoValidator {
    const val MIN_LENGTH = 3
    const val MAX_LENGTH = 20

    private val allowedChars = Regex("^[A-Za-z0-9_.]+$")

    fun validate(pseudo: String): Set<PseudoValidationError> = buildSet {
        val trimmed = pseudo.trim()
        if (trimmed.length !in MIN_LENGTH..MAX_LENGTH) add(PseudoValidationError.InvalidLength)
        if (trimmed.isNotEmpty() && !allowedChars.matches(trimmed)) add(PseudoValidationError.InvalidChars)
        if (trimmed.startsWith(".") || trimmed.endsWith(".")) add(PseudoValidationError.InvalidDot)
    }
}
