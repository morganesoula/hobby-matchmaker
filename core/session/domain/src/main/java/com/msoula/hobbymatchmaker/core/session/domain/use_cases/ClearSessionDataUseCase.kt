package com.msoula.hobbymatchmaker.core.session.domain.use_cases

import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository

class ClearSessionDataUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke() = sessionRepository.clearSessionData()
}
