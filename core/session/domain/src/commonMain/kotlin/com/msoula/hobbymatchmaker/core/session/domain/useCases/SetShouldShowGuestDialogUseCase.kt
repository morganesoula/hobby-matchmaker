package com.msoula.hobbymatchmaker.core.session.domain.useCases

import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository

class SetShouldShowGuestDialogUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(shouldShow: Boolean) =
        sessionRepository.setShouldShowGuestDialog(shouldShow)
}
