package com.msoula.hobbymatchmaker.features.profile.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
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
import kotlinx.coroutines.flow.onStart

class UserProfileRepositoryImpl(
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val socialLocalDataSource: SocialLocalDataSource,
    private val userProfileRemoteDataSource: UserProfileRemoteDataSource,
    private val socialRemoteDataSource: SocialRemoteDataSource
) : UserProfileRepository {

    override fun observeCurrentUserProfile(): Flow<UserProfileDomainModel?> =
        combine(
            userProfileLocalDataSource.observeCurrentUserProfile(),
            socialLocalDataSource.observeSocialCircle()
                .onStart { emit(emptyList()) }
        ) { profile, members ->
            if (profile == null) {
                Logger.d("No profile found in local storage")
                null
            } else {
                Logger.d("Inside repoImpl with profile: $profile and members: $members")
                UserProfileDomainModel(
                    uid = profile.uid,
                    name = profile.name,
                    avatarUrl = profile.avatarUrl,
                    bio = profile.bio,
                    interests = profile.interests,
                    likedMoviesCount = profile.likedCount,
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
}
