package com.msoula.hobbymatchmaker.features.profile.domain.useCases

import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveCurrentUserProfileStateUseCase(
    private val userProfileRepository: UserProfileRepository
) {
    operator fun invoke(): Flow<UserProfileDomainModel> =
        userProfileRepository.observeCurrentUserProfile()
            .map {
                it ?: UserProfileDomainModel.empty()
            }
            .distinctUntilChanged()
}
