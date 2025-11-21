package com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileRemoteDataModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import kotlinx.coroutines.flow.Flow

class UserProfileRemoteDataSourceImpl: UserProfileRemoteDataSource {
    override fun observeCurrentUserProfile(): Flow<UserProfileRemoteDataModel> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshCurrentUserProfile() {
        TODO("Not yet implemented")
    }

    override suspend fun checkIfPseudoIsAvailable(pseudo: String): AppResult<Boolean, AppError> {
        TODO("Not yet implemented")
    }
}
