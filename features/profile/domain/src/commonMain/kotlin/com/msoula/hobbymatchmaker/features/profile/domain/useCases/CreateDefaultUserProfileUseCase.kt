package com.msoula.hobbymatchmaker.features.profile.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository
import kotlinx.coroutines.flow.first

class CreateDefaultUserProfileUseCase(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(uid: String, name: String = ""): AppResult<Unit, AppError> {
        val existingProfile = userProfileRepository.observeCurrentUserProfile(uid).first()

        // TODO update pseudo
        return if (existingProfile == null) {
            userProfileRepository.upsertUserProfile(
                UserProfileDomainModel(
                    uid = uid,
                    name = name,
                    pseudo = "",
                    avatarUrl = null,
                    bio = null,
                    interests = emptyList(),
                    likedMoviesCount = 0,
                    socialCircle = emptyList()
                )
            )
        } else {
            AppResult.Success(Unit)
        }
    }
}
