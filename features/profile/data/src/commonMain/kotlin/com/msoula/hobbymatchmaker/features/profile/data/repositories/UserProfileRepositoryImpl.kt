package com.msoula.hobbymatchmaker.features.profile.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.SocialLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.UserProfileLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.SocialRemoteDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.UserProfileRemoteDataSource
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProfileRepositoryImpl(
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val userProfileRemoteDataSource: UserProfileRemoteDataSource,
    private val socialLocalDataSource: SocialLocalDataSource,
    private val socialRemoteDataSource: SocialRemoteDataSource
) : UserProfileRepository {

    override suspend fun observeCurrentUserProfile(): Flow<UserProfileDomainModel> =
        userProfileLocalDataSource.observeCurrentUserProfile().map {
            it
        }


    override suspend fun refreshUserProfile(userProfileDomainModel: UserProfileDomainModel) {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentUserProfile(): AppResult<UserProfileDomainModel, AppError> {
        TODO("Not yet implemented")
    }
}
