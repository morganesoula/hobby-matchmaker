package com.msoula.hobbymatchmaker.features.profile.domain.useCases

import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository

class IsPseudoAvailableUseCase(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(pseudo: String) =
        userProfileRepository.checkIfPseudoIsAvailable(pseudo)
}
