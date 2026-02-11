package com.msoula.hobbymatchmaker.core.user.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.user.domain.models.UserSummaryDomainModel
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    fun observeUser(uid: String): Flow<UserSummaryDomainModel?>
    fun observeUsers(uids: List<String>): Flow<Map<String, UserSummaryDomainModel>>
    suspend fun getUser(uid: String): AppResult<UserSummaryDomainModel, AppError>
    suspend fun getUsers(uids: List<String>): Map<String, UserSummaryDomainModel>
    suspend fun prefetchUsers(uids: List<String>)
    suspend fun invalidateCache(uid: String)
}
