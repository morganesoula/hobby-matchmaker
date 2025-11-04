package com.msoula.hobbymatchmaker.core.authentication.domain.models

import com.msoula.hobbymatchmaker.core.common.AppError

sealed interface AuthenticationResult {
    data class Success(val user: AuthenticatedUser) : AuthenticationResult
    data class Failure(val error: AppError) : AuthenticationResult
}
