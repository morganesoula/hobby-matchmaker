package com.msoula.hobbymatchmaker.features.profile.domain.models

sealed interface PseudoValidationError {
    data object InvalidLength : PseudoValidationError
    data object InvalidChars : PseudoValidationError
    data object InvalidDot : PseudoValidationError
}
