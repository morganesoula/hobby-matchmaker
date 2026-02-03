package com.msoula.hobbymatchmaker.core.user.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.user.domain.models.UserSummaryDomainModel
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {
    fun observeUser(uid: String): Flow<UserSummaryDomainModel?>
    suspend fun getUser(uid: String): UserSummaryDomainModel?
    suspend fun getUsers(uids: List<String>): Map<String, UserSummaryDomainModel>
}
