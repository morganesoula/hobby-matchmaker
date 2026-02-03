package com.msoula.hobbymatchmaker.core.database.services

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.msoula.hobbymatchmaker.core.common.DispatcherProvider
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.database.mappers.toUserProfileDataEntity
import com.msoula.hobbymatchmaker.core.database.models.UserProfileDataEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class UserProfileDAOImpl(
    private val database: HMMDatabase,
    private val dispatcherProvider: DispatcherProvider
) : UserProfileDAO {
    override suspend fun insertUserProfile(userProfile: UserProfileDataEntity) {
        database.hmm_databaseQueries.insertUserProfile(
            userProfile.uid,
            userProfile.name,
            userProfile.pseudo,
            userProfile.avatarUrl,
            userProfile.bio,
            userProfile.interests,
            userProfile.likedCount.toLong(),
            userProfile.circleCount.toLong(),
            Clock.System.now().toEpochMilliseconds()
        )
    }

    override suspend fun upsertUserProfile(userProfile: UserProfileDataEntity) {
        database.transaction {
            val existingUserProfile =
                database.hmm_databaseQueries.selectUserById(userProfile.uid).executeAsOneOrNull()

            if (existingUserProfile == null) {
                database.hmm_databaseQueries.insertUserProfile(
                    uid = userProfile.uid,
                    name = userProfile.name,
                    pseudo = userProfile.pseudo,
                    avatar_url = userProfile.avatarUrl,
                    bio = userProfile.bio,
                    interests_json = userProfile.interests,
                    liked_count = userProfile.likedCount.toLong(),
                    circle_count = userProfile.circleCount.toLong(),
                    updated_at = Clock.System.now().toEpochMilliseconds()
                )
            } else {
                database.hmm_databaseQueries.updateUserProfile(
                    userProfile.name,
                    userProfile.avatarUrl,
                    userProfile.pseudo,
                    userProfile.bio,
                    userProfile.interests,
                    userProfile.likedCount.toLong(),
                    userProfile.circleCount.toLong(),
                    Clock.System.now().toEpochMilliseconds(),
                    userProfile.uid
                )
            }
        }

    }

    override fun observeUserProfile(uid: String): Flow<UserProfileDataEntity?> {
        return database.hmm_databaseQueries
            .selectUserById(uid)
            .asFlow()
            .mapToOneOrNull(dispatcherProvider.io)
            .map { row ->
                row?.toUserProfileDataEntity()
            }
    }

    override suspend fun getUserProfileByPseudo(pseudo: String): UserProfileDataEntity? {
        val row = database.hmm_databaseQueries
            .selectUserByPseudo(pseudo)
            .executeAsOneOrNull()

        return row?.toUserProfileDataEntity()
    }
}
