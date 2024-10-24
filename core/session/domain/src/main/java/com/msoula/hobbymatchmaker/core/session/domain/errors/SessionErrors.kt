package com.msoula.hobbymatchmaker.core.session.domain.errors

import com.msoula.hobbymatchmaker.core.common.AppError

sealed class SessionErrors : AppError {
    class CreateUserError(override val message: String) : SessionErrors()
}
