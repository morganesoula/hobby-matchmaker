package com.msoula.hobbymatchmaker.features.profile.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeCurrentUserProfile(): Flow<UserProfileDomainModel?>
    suspend fun refreshUserProfile(userProfileDomainModel: UserProfileDomainModel)
    suspend fun upsertUserProfile(userProfileDomainModel: UserProfileDomainModel): AppResult<Unit, AppError>
}
