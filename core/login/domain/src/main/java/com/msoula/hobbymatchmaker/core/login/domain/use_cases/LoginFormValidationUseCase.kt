package com.msoula.hobbymatchmaker.core.login.domain.use_cases

data class LoginFormValidationUseCase(
    val validateEmail: ValidateEmailUseCase,
    val validatePassword: ValidatePasswordUseCase,
    val validateFirstName: ValidateNameUseCase,
    val validateLastName: ValidateNameUseCase,
)
