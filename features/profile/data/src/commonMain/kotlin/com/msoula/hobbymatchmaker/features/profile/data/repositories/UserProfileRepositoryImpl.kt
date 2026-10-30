package com.msoula.hobbymatchmaker.features.profile.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.UserProfileLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers.toUserProfileLocalDataModel
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers.toUserProfileNoCircleDomainModel
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers.toUserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers.toUserProfileRemoteDataModel
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.UserProfileRemoteDataSource
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileNoCircleDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProfileRepositoryImpl(
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val userProfileRemoteDataSource: UserProfileRemoteDataSource,
) : UserProfileRepository {

    override fun observeCurrentUserProfile(uid: String): Flow<UserProfileNoCircleDomainModel?> =
        userProfileLocalDataSource.observeCurrentUserProfile(uid)
            .map { it?.toUserProfileNoCircleDomainModel() }

    override suspend fun syncUserProfile(userProfileDomainModel: UserProfileDomainModel): AppResult<Unit, AppError> {
        return userProfileRemoteDataSource.syncUserProfile(
            userProfileDomainModel.toUserProfileRemoteDataModel()
        )
    }

    override suspend fun upsertUserProfile(userProfileDomainModel: UserProfileDomainModel) =
        userProfileLocalDataSource.upsertUserProfile(userProfileDomainModel.toUserProfileLocalDataModel())

    override suspend fun checkIfPseudoIsAvailable(pseudo: String): AppResult<Boolean, AppError> =
        userProfileRemoteDataSource.checkIfPseudoIsAvailable(pseudo)

    override suspend fun fetchRemoteUserProfile(uid: String): AppResult<UserProfileDomainModel?, AppError> =
        userProfileRemoteDataSource.fetchUserProfile(uid).mapSuccess { it?.toUserProfileDomainModel() }

    override suspend fun reservePseudo(uid: String, pseudo: String, name: String): AppResult<Unit, AppError> =
        userProfileRemoteDataSource.reservePseudo(uid, pseudo, name)

    override suspend fun ensurePseudoReservation(uid: String, pseudo: String): AppResult<Unit, AppError> =
        userProfileRemoteDataSource.ensurePseudoReservation(uid, pseudo)
}
