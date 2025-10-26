package com.msoula.hobbymatchmaker.features.profile.domain.useCases

import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository

class ObserveCurrentUserProfileUseCase(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke() = userProfileRepository.observeCurrentUserProfile()
}
