package com.msoula.hobbymatchmaker.core.session.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository

class CreateUserUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke(userDomainModel: SessionUserDomainModel): AppResult<Unit, AppError> =
        sessionRepository.createUser(userDomainModel)
}
