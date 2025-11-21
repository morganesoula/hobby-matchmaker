package com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileRemoteDataModel
import kotlinx.coroutines.flow.Flow

interface UserProfileRemoteDataSource {
    fun observeCurrentUserProfile(): Flow<UserProfileRemoteDataModel>
    suspend fun refreshCurrentUserProfile()
    suspend fun checkIfPseudoIsAvailable(pseudo: String): AppResult<Boolean, AppError>
}
