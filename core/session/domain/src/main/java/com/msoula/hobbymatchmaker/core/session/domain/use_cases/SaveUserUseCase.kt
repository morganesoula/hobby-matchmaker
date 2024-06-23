package com.msoula.hobbymatchmaker.core.session.domain.use_cases

import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository

class SaveUserUseCase(private val sessionRepository: SessionRepository) {

    suspend operator fun invoke(userDomainModel: SessionUserDomainModel) =
        sessionRepository.saveUser(userDomainModel)
}
