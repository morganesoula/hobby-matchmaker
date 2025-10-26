package com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote

import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileRemoteDataModel
import kotlinx.coroutines.flow.Flow

class UserProfileRemoteDataSourceImpl: UserProfileRemoteDataSource {
    override fun observeCurrentUserProfile(): Flow<UserProfileRemoteDataModel> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshCurrentUserProfile() {
        TODO("Not yet implemented")
    }
}
