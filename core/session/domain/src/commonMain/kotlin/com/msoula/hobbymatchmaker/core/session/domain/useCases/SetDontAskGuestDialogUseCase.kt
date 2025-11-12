package com.msoula.hobbymatchmaker.core.session.domain.useCases

import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class SetDontAskGuestDialogUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(dontAsk: Boolean) =
        sessionRepository.setDontAskGuestDialogValue(dontAsk)

}
