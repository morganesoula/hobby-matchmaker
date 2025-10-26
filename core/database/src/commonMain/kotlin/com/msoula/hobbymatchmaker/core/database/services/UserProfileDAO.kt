package com.msoula.hobbymatchmaker.core.database.services

import com.msoula.hobbymatchmaker.core.database.models.UserProfileDataEntity
import kotlinx.coroutines.flow.Flow

interface UserProfileDAO {
    suspend fun insertUserProfile(userProfile: UserProfileDataEntity)
    suspend fun updateExistingUserProfile(userProfile: UserProfileDataEntity)
    fun observeUserProfile(uid: String): Flow<UserProfileDataEntity?>
}
