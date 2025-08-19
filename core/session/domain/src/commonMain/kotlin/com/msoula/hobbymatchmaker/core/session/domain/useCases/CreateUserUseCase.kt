package com.msoula.hobbymatchmaker.core.session.domain.useCases

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.errors.SessionErrors
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository

class CreateUserUseCase(private val sessionRepository: SessionRepository) {

    suspend operator fun invoke(userDomainModel: SessionUserDomainModel):
        Result<Boolean, SessionErrors.CreateUserErrorHMM> =
        sessionRepository.createUser(userDomainModel)
}
