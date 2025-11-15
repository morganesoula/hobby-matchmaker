package com.msoula.hobbymatchmaker.core.session.domain.useCases

import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository

class ClearCurrentUserProfileUuidUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke() = sessionRepository.clearCurrentUserUid()
}
