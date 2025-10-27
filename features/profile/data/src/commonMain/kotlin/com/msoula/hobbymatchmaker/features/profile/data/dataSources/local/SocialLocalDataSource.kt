package com.msoula.hobbymatchmaker.features.profile.data.dataSources.local

import com.msoula.hobbymatchmaker.features.profile.data.models.SocialMemberLocalDataModel
import kotlinx.coroutines.flow.Flow

interface SocialLocalDataSource {
    fun observeSocialCircle(): Flow<List<SocialMemberLocalDataModel>>
    fun observeSocialCircleCount(): Flow<Int>
    suspend fun addToCircle(member: SocialMemberLocalDataModel)
    suspend fun removeFromCircle(memberUid: String)
}
