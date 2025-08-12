package com.msoula.hobbymatchmaker.core.session.domain.useCases

import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository

class ObserveShouldShowGuestDialogUseCase(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke() = sessionRepository.observeShouldShowGuestDialog()
}
