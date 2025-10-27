package com.msoula.hobbymatchmaker.features.profile.data.repositories

import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.SocialLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.UserProfileLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.SocialRemoteDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.UserProfileRemoteDataSource
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel.Companion.DEFAULT_AVATAR_URL
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel.Companion.DEFAULT_NAME
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull

class UserProfileRepositoryImpl(
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val userProfileRemoteDataSource: UserProfileRemoteDataSource,
    private val socialLocalDataSource: SocialLocalDataSource,
    private val socialRemoteDataSource: SocialRemoteDataSource
) : UserProfileRepository {

    override fun observeCurrentUserProfile(): Flow<UserProfileDomainModel> =
        combine(
            userProfileLocalDataSource.observeCurrentUserProfile(),
            socialLocalDataSource.observeSocialCircle()
        ) { profile, members ->
            if (profile == null) {
                null
            } else {
                UserProfileDomainModel(
                    uid = profile.uid,
                    name = profile.name,
                    avatarUrl = profile.avatarUrl,
                    bio = profile.bio,
                    interests = profile.interests,
                    likedMoviesCount = profile.likedCount,
                    socialCircle = members.map {
                        UserSummaryDomainModel(
                            uid = it.memberId,
                            name = it.name ?: DEFAULT_NAME,
                            avatarUrl = it.avatarUrl ?: DEFAULT_AVATAR_URL
                        )
                    },
                )
            }
        }.filterNotNull()


    override suspend fun refreshUserProfile(userProfileDomainModel: UserProfileDomainModel) {
        TODO("Not yet implemented")
    }
}
