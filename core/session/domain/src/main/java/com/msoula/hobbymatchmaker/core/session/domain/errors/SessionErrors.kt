package com.msoula.hobbymatchmaker.core.session.domain.errors

import com.msoula.hobbymatchmaker.core.common.AppError

sealed class SessionErrors {
    sealed class CreateUserError(override val message: String) : AppError {
        data class SaveError(val saveErrorMessage: String) :
            CreateUserError("Error while saving user in firestore")
    }
}
