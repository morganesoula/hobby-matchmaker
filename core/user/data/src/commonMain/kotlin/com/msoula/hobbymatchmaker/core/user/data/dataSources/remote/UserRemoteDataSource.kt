package com.msoula.hobbymatchmaker.core.user.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.user.domain.models.UserSummaryDomainModel
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {
    fun observeUser(uid: String): Flow<UserSummaryDomainModel?>
    suspend fun getUser(uid: String): AppResult<UserSummaryDomainModel?, AppError>
    suspend fun getUsers(uids: List<String>): Map<String, UserSummaryDomainModel>
}
