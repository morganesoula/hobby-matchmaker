package com.msoula.hobbymatchmaker.core.session.domain.useCases

import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository

class SetIsConnectedUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke(isConnected: Boolean) =
        sessionRepository.setIsConnected(isConnected)
}
