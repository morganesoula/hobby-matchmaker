package com.msoula.hobbymatchmaker.core.session.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository

class RemoveLegacyUserEmailUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke(uid: String): AppResult<Unit, AppError> =
        sessionRepository.removeLegacyEmail(uid)
}
