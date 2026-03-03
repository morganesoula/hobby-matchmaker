package com.msoula.hobbymatchmaker.core.login.presentation.orchestrators

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveDontAskCheckboxValueUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetCurrentUserProfileUuidUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetDontAskGuestDialogUseCase
import kotlinx.coroutines.flow.Flow

class SessionManager(
    private val setDontAskGuestDialogUseCase: SetDontAskGuestDialogUseCase,
    private val observeDontAskCheckboxValueUseCase: ObserveDontAskCheckboxValueUseCase,
    private val setCurrentUserProfileUuidUseCase: SetCurrentUserProfileUuidUseCase
) {
    suspend fun setDontAskGuestDialog(dontAsk: Boolean) {
        setDontAskGuestDialogUseCase(dontAsk)
    }

    fun observeDontAskCheckboxValue(): Flow<Boolean> {
        return observeDontAskCheckboxValueUseCase()
    }

    suspend fun setCurrentUserProfileUuid(authenticatedUid: String? = null): AppResult<String, AppError> {
        return setCurrentUserProfileUuidUseCase(authenticatedUid)
    }
}
