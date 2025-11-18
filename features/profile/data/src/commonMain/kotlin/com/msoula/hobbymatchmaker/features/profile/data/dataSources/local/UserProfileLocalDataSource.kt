package com.msoula.hobbymatchmaker.features.profile.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileLocalDataModel
import kotlinx.coroutines.flow.Flow

interface UserProfileLocalDataSource {
    fun observeCurrentUserProfile(): Flow<UserProfileLocalDataModel?>
    suspend fun upsertUserProfile(userProfile: UserProfileLocalDataModel): AppResult<Unit, AppError>
}
