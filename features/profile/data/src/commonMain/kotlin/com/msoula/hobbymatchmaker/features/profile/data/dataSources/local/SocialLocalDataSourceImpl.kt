package com.msoula.hobbymatchmaker.features.profile.data.dataSources.local

import kotlinx.coroutines.flow.Flow

class SocialLocalDataSourceImpl: SocialLocalDataSource {
    override suspend fun getSocialCircleCount(): Flow<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun getSocialCircle(): Flow<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToCircle(memberUid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromCircle(memberUid: String) {
        TODO("Not yet implemented")
    }
}
