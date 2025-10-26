package com.msoula.hobbymatchmaker.features.profile.domain.useCases

import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository

class RefreshUserProfileUseCase(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(userProfileDomainModel: UserProfileDomainModel) =
        userProfileRepository.refreshUserProfile(userProfileDomainModel)
}
