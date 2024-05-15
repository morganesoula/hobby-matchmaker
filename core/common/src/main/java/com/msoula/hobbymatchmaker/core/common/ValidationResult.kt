package com.msoula.hobbymatchmaker.core.common

data class ValidationResult(
    val successful: Boolean = false,
    val errorMessage: Int? = null,
)
