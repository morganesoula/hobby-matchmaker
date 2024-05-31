package com.msoula.hobbymatchmaker.core.session.domain.use_cases

import com.msoula.hobbymatchmaker.core.session.domain.repositories.UserRepository

class ClearUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.clearUser()
}
