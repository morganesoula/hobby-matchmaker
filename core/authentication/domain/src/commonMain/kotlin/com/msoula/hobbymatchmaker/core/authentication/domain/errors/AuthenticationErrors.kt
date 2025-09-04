package com.msoula.hobbymatchmaker.core.authentication.domain.errors

data class InvalidCredentialError(override val message: String) : RuntimeException(message)
