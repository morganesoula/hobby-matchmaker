package com.msoula.di.domain

data class ValidationResult(
    val successful: Boolean = false,
    val errorMessage: Int? = null,
)
