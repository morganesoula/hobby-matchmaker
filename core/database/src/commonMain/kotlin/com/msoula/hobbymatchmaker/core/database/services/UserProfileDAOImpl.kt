package com.msoula.hobbymatchmaker.core.database.services

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.database.models.UserProfileDataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class UserProfileDAOImpl(
    private val database: HMMDatabase
) : UserProfileDAO {
    override suspend fun insertUserProfile(userProfile: UserProfileDataEntity) {
        database.hmm_databaseQueries.insertUserProfile(
            userProfile.uid,
            userProfile.name,
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
            .mapToOneOrNull(Dispatchers.IO)
            .map { row ->
                row?.let {
                    UserProfileDataEntity(
                        uid = it.uid,
                        name = it.name,
                        avatarUrl = it.avatar_url,
                        bio = it.bio,
                        interests = it.interests_json,
                        likedCount = it.liked_count.toInt(),
                        circleCount = it.circle_count.toInt()
                    )
                }
            }
    }
}
