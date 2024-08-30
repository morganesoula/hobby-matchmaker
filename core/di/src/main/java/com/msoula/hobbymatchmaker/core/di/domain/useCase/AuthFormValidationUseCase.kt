package com.msoula.hobbymatchmaker.core.di.domain.useCase

data class AuthFormValidationUseCase(
    val validateEmailUseCase: ValidateEmailUseCase,
    val validatePasswordUseCase: ValidatePasswordUseCase,
    val validateFirstNameUseCase: ValidateNameUseCase,
    val validateLastNameUseCase: ValidateNameUseCase,
)
