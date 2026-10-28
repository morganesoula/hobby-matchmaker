package com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileRemoteDataModel

interface UserProfileRemoteDataSource {
    suspend fun syncUserProfile(userProfileRemoteDataModel: UserProfileRemoteDataModel)
        : AppResult<Unit, AppError>

    suspend fun fetchUserProfile(uid: String): AppResult<UserProfileRemoteDataModel?, AppError>

    suspend fun checkIfPseudoIsAvailable(userPseudo: String): AppResult<Boolean, AppError>

    suspend fun reservePseudo(uid: String, pseudo: String, name: String): AppResult<Unit, AppError>

    suspend fun ensurePseudoReservation(uid: String, pseudo: String): AppResult<Unit, AppError>
}
