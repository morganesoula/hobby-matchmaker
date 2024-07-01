package com.msoula.hobbymatchmaker.core.login.domain.use_cases

data class LoginValidateFormUseCase(
    val validateEmail: ValidateEmailUseCase,
    val validatePassword: ValidatePasswordUseCase,
    val validateFirstName: ValidateNameUseCase
)
