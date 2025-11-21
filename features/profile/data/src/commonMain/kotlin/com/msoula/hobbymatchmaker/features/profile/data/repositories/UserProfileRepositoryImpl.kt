package com.msoula.hobbymatchmaker.features.profile.data.repositories

import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.SocialLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.UserProfileLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers.toUserProfileLocalDataModel
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.SocialRemoteDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.UserProfileRemoteDataSource
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel.Companion.DEFAULT_AVATAR_URL
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel.Companion.DEFAULT_NAME
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart

class UserProfileRepositoryImpl(
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val socialLocalDataSource: SocialLocalDataSource,
    private val userProfileRemoteDataSource: UserProfileRemoteDataSource,
    private val socialRemoteDataSource: SocialRemoteDataSource,
    private val movieLocalDataSource: MovieLocalDataSource
) : UserProfileRepository {

    override fun observeCurrentUserProfile(uid: String): Flow<UserProfileDomainModel?> =
        combine(
            userProfileLocalDataSource.observeCurrentUserProfile(uid),
            movieLocalDataSource.observeMoviesLikedCount(),
            socialLocalDataSource.observeSocialCircle()
                .onStart { emit(emptyList()) }
        ) { profile, count, members ->
            if (profile == null) {
                Logger.d("No profile found in local storage")
                null
            } else {
                UserProfileDomainModel(
                    uid = profile.uid,
                    name = profile.name,
                    avatarUrl = profile.avatarUrl,
                    bio = profile.bio,
                    interests = profile.interests,
                    likedMoviesCount = count.toInt(),
                    socialCircle =
                        members.map {
                            UserSummaryDomainModel(
                                uid = it.memberId,
                                name = it.name ?: DEFAULT_NAME,
                                avatarUrl = it.avatarUrl ?: DEFAULT_AVATAR_URL
                            )
                        }
                )
            }
        }


    override suspend fun refreshUserProfile(userProfileDomainModel: UserProfileDomainModel) {
        TODO("Not yet implemented")
    }

    override suspend fun upsertUserProfile(userProfileDomainModel: UserProfileDomainModel) =
        userProfileLocalDataSource.upsertUserProfile(userProfileDomainModel.toUserProfileLocalDataModel())
}
