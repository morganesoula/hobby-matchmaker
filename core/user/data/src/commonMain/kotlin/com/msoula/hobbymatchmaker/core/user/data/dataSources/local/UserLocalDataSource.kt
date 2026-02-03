package com.msoula.hobbymatchmaker.core.user.data.dataSources.local

import com.msoula.hobbymatchmaker.core.user.domain.models.UserSummaryDomainModel
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {
    fun observeUsers(uids: List<String>): Flow<List<UserSummaryDomainModel?>>
    suspend fun getUser(uid: String): UserSummaryDomainModel?
    suspend fun getUsers(uids: List<String>): List<UserSummaryDomainModel?>
    suspend fun upsertUser(user: UserSummaryDomainModel)
    suspend fun upsertUsers(users: List<UserSummaryDomainModel?>)
    suspend fun deleteUser(uid: String)
}
