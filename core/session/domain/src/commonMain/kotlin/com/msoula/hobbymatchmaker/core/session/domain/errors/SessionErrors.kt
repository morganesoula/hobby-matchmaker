package com.msoula.hobbymatchmaker.core.session.domain.errors

import com.msoula.hobbymatchmaker.core.common.HMMAppError

sealed class SessionErrors {
    sealed class CreateUserErrorHMM(override val message: String) : HMMAppError {
        data class SaveErrorHMM(val saveErrorMessage: String) :
            CreateUserErrorHMM("Error while saving user in firestore")
    }
}
