package com.msoula.hobbymatchmaker.features.profile.data.dataSources.local

import kotlinx.coroutines.flow.Flow

interface SocialLocalDataSource {
    suspend fun getSocialCircleCount(): Flow<Int>
    suspend fun getSocialCircle(): Flow<List<String>>
    suspend fun addToCircle(memberUid: String)
    suspend fun removeFromCircle(memberUid: String)
}
