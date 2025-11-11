package com.msoula.hobbymatchmaker.core.session.domain.useCases

import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository

class ObserveDontAskCheckboxValueUseCase(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke() = sessionRepository.observeDontAskCheckboxValue()
}
