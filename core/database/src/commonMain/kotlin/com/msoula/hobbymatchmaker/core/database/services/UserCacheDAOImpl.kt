package com.msoula.hobbymatchmaker.core.database.services

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.msoula.hobbymatchmaker.core.common.DispatcherProvider
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.database.mappers.toUserCacheDataEntity
import com.msoula.hobbymatchmaker.core.database.models.UserCacheDataEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserCacheDAOImpl(
    private val database: HMMDatabase,
    private val dispatcherProvider: DispatcherProvider
) : UserCacheDAO {
    override fun observeUser(uid: String): Flow<UserCacheDataEntity?> =
        database.hmm_databaseQueries.observeUserByUid(uid).asFlow().mapToOne(dispatcherProvider.io)
            .map { userCache ->
                userCache.toUserCacheDataEntity()
            }

    override fun observeUsers(uids: List<String>): Flow<List<UserCacheDataEntity>> =
        database.hmm_databaseQueries.observeUsersByUids(uids).asFlow().mapToList(dispatcherProvider.io)
            .map { rows ->
                if (rows.isEmpty()) return@map emptyList()

                rows.map { user ->
                    user.toUserCacheDataEntity()
                }
            }

    override suspend fun getUser(uid: String): UserCacheDataEntity? =
        database.hmm_databaseQueries.selectUserByUid(uid).executeAsOneOrNull()
            ?.toUserCacheDataEntity()


    override suspend fun getUsers(uids: List<String>): List<UserCacheDataEntity> =
        database.hmm_databaseQueries.selectUsersByUids(uids).executeAsList()
            .map { it.toUserCacheDataEntity() }

    override suspend fun upsertUser(user: UserCacheDataEntity) {
        database.hmm_databaseQueries.upsertUser(
            uid = user.uid,
            pseudo = user.pseudo,
            name = user.name,
            avatar_url = user.avatarUrl,
            movies_liked_json = user.moviesLikedJson,
            updated_at = user.updatedAt
        )
    }

    override suspend fun upsertUsers(users: List<UserCacheDataEntity>) {
        users.forEach { user ->
            database.hmm_databaseQueries.upsertUser(
                uid = user.uid,
                pseudo = user.pseudo,
                name = user.name,
                avatar_url = user.avatarUrl,
                movies_liked_json = user.moviesLikedJson,
                updated_at = user.updatedAt
            )
        }
    }

    override suspend fun deleteUser(uid: String) {
        database.hmm_databaseQueries.deleteUser(uid)
    }
}
