package com.msoula.hobbymatchmaker.features.profile.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeCallStorage
import com.msoula.hobbymatchmaker.core.database.services.UserProfileDAO
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers.toUserProfileDataEntity
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers.toUserProfileLocalDataModel
import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileLocalDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserProfileLocalDataSourceImpl(
    private val userProfileDAO: UserProfileDAO
) : UserProfileLocalDataSource {

    override fun observeCurrentUserProfile(uid: String): Flow<UserProfileLocalDataModel?> =
        flow {
            emitAll(
                userProfileDAO.observeUserProfile(uid).map { it?.toUserProfileLocalDataModel() }
            )
        }

    override suspend fun upsertUserProfile(userProfile: UserProfileLocalDataModel): AppResult<Unit, AppError> {
        return safeCallStorage {
            userProfileDAO.upsertUserProfile(userProfile.toUserProfileDataEntity())
        }
    }
}
