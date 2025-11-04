package com.msoula.hobbymatchmaker.features.profile.data.dataSources.local

import com.msoula.hobbymatchmaker.core.database.services.UserProfileDAO
import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.SessionLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers.toUserProfileDataEntity
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers.toUserProfileLocalDataModel
import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileLocalDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserProfileLocalDataSourceImpl(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val userProfileDAO: UserProfileDAO
) : UserProfileLocalDataSource {

    override fun observeCurrentUserProfile(): Flow<UserProfileLocalDataModel?> =
        flow {
            val uid = sessionLocalDataSource.observeCurrentUid().first()
            emitAll(
                userProfileDAO.observeUserProfile(uid).map { it?.toUserProfileLocalDataModel() }
            )
        }

    override suspend fun insertUserProfile(userProfile: UserProfileLocalDataModel) {
        userProfileDAO.insertUserProfile(userProfile.toUserProfileDataEntity())
    }

    override suspend fun updateUserProfile(userProfile: UserProfileLocalDataModel) {
        userProfileDAO.updateExistingUserProfile(userProfile.toUserProfileDataEntity())
    }
}
