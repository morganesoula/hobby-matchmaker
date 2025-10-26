package com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote

import kotlinx.coroutines.flow.Flow

interface SocialRemoteDataSource {
    fun observeSocialCircleCount(): Flow<Int>
    fun observeSocialCircle(): Flow<List<String>>
    suspend fun addToCircle(memberUid: String)
    suspend fun removeFromCircle(memberUid: String)
}
