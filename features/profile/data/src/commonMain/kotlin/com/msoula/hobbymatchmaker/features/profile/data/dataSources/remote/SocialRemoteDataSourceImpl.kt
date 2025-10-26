package com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote

import kotlinx.coroutines.flow.Flow

class SocialRemoteDataSourceImpl: SocialRemoteDataSource {
    override fun observeSocialCircleCount(): Flow<Int> {
        TODO("Not yet implemented")
    }

    override fun observeSocialCircle(): Flow<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToCircle(memberUid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromCircle(memberUid: String) {
        TODO("Not yet implemented")
    }
}
