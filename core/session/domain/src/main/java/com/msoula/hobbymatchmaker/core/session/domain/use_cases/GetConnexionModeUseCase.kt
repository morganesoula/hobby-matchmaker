package com.msoula.hobbymatchmaker.core.session.domain.use_cases

import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository

class GetConnexionModeUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke() = sessionRepository.getConnexionMode()
}
