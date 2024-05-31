package com.msoula.hobbymatchmaker.core.session.domain.use_cases

import com.msoula.hobbymatchmaker.core.session.domain.models.UserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.repositories.UserRepository

class SaveUserUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(userDomainModel: UserDomainModel) =
        userRepository.saveUser(userDomainModel)
}
