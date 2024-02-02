package com.msoula.di.domain.use_case

data class AuthFormValidationUseCase(
    val validateEmail: ValidateEmail,
    val validatePassword: ValidatePassword,
    val validateFirstName: ValidateName,
    val validateLastName: ValidateName
)
