package com.msoula.hobbymatchmaker.core.user.data.dataSources.local

import com.msoula.hobbymatchmaker.core.database.models.UserCacheDataEntity
import com.msoula.hobbymatchmaker.core.database.services.UserCacheDAO
import com.msoula.hobbymatchmaker.core.user.data.dataSources.local.mappers.toUserCacheDataEntity
import com.msoula.hobbymatchmaker.core.user.data.dataSources.local.mappers.toUserSummaryDomainModel
import com.msoula.hobbymatchmaker.core.user.domain.models.UserSummaryDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserLocalDataSourceImpl(
    private val userCacheDAO: UserCacheDAO
) : UserLocalDataSource {
    override fun observeUsers(uids: List<String>): Flow<List<UserSummaryDomainModel?>> =
        userCacheDAO.observeUsers(uids).map { entities ->
            entities.map { it.toUserSummaryDomainModel() }
        }

    override suspend fun getUser(uid: String): UserSummaryDomainModel? =
        userCacheDAO.getUser(uid)?.toUserSummaryDomainModel()

    override suspend fun getUsers(uids: List<String>): List<UserSummaryDomainModel?> =
        userCacheDAO.getUsers(uids).map { user -> user.toUserSummaryDomainModel() }

    override suspend fun upsertUser(user: UserSummaryDomainModel) =
        userCacheDAO.upsertUser(user.toUserCacheDataEntity())

    override suspend fun upsertUsers(users: List<UserSummaryDomainModel?>) =
        userCacheDAO.upsertUsers(users.map { user ->
            user?.toUserCacheDataEntity() ?: UserCacheDataEntity.Initial
        })

    override suspend fun deleteUser(uid: String) =
        userCacheDAO.deleteUser(uid)
}
