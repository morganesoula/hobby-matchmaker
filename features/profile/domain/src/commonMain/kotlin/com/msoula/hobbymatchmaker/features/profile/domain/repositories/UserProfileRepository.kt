package com.msoula.hobbymatchmaker.features.profile.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileNoCircleDomainModel
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeCurrentUserProfile(uid: String): Flow<UserProfileNoCircleDomainModel?>
    suspend fun syncUserProfile(userProfileDomainModel: UserProfileDomainModel): AppResult<Unit, AppError>
    suspend fun upsertUserProfile(userProfileDomainModel: UserProfileDomainModel): AppResult<Unit, AppError>
    suspend fun checkIfPseudoIsAvailable(pseudo: String): AppResult<Boolean, AppError>
}
