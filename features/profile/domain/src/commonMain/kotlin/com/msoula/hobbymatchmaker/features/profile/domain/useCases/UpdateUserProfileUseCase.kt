package com.msoula.hobbymatchmaker.features.profile.domain.useCases

import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository

class UpdateUserProfileUseCase(
    private val userProfileRepository: UserProfileRepository
) {
}
