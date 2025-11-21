package com.msoula.hobbymatchmaker.core.database.services

import com.msoula.hobbymatchmaker.core.database.models.UserProfileDataEntity
import kotlinx.coroutines.flow.Flow

interface UserProfileDAO {
    suspend fun insertUserProfile(userProfile: UserProfileDataEntity)
    suspend fun upsertUserProfile(userProfile: UserProfileDataEntity)
    fun observeUserProfile(uid: String): Flow<UserProfileDataEntity?>
    suspend fun getUserProfileByPseudo(pseudo: String): UserProfileDataEntity?
}
