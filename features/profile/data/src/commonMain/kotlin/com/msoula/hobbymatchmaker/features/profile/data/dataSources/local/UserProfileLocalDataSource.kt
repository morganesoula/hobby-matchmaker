package com.msoula.hobbymatchmaker.features.profile.data.dataSources.local

import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileLocalDataModel
import kotlinx.coroutines.flow.Flow

interface UserProfileLocalDataSource {
    fun observeCurrentUserProfile(uid: String): Flow<UserProfileLocalDataModel?>
    suspend fun insertUserProfile(userProfile: UserProfileLocalDataModel)
    suspend fun updateUserProfile(userProfile: UserProfileLocalDataModel)
}
