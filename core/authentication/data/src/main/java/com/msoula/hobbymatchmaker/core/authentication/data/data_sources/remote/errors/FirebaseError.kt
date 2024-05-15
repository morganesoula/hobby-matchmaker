package com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors

import com.msoula.hobbymatchmaker.core.common.AppError

class CreateUserError(override val message: String) : AppError
class SignInError(override val message: String) : AppError
class ResetPasswordError(override val message: String) : AppError
