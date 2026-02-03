package com.msoula.hobbymatchmaker.core.database.services

import com.msoula.hobbymatchmaker.core.database.models.UserCacheDataEntity
import kotlinx.coroutines.flow.Flow

interface UserCacheDAO {
    fun observeUser(uid: String): Flow<UserCacheDataEntity?>
    fun observeUsers(uids: List<String>): Flow<List<UserCacheDataEntity>>
    suspend fun getUser(uid: String): UserCacheDataEntity?
    suspend fun getUsers(uids: List<String>): List<UserCacheDataEntity>
    suspend fun upsertUser(user: UserCacheDataEntity)
    suspend fun upsertUsers(users: List<UserCacheDataEntity>)
    suspend fun deleteUser(uid: String)
}
