package com.msoula.hobbymatchmaker.features.profile.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.UserProfileLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers.toUserProfileLocalDataModel
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers.toUserProfileRemoteDataModel
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.UserProfileRemoteDataSource
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart

class UserProfileRepositoryImpl(
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val userProfileRemoteDataSource: UserProfileRemoteDataSource,
    private val socialRepository: SocialRepository,
    private val movieRepository: MovieRepository
) : UserProfileRepository {

    override fun observeCurrentUserProfile(uid: String): Flow<UserProfileDomainModel?> =
        combine(
            userProfileLocalDataSource.observeCurrentUserProfile(uid),
            movieRepository.observeMoviesLikedCount(),
            socialRepository.observeSocialCircle(uid)
                .onStart { emit(emptyList()) }
        ) { profile, count, members ->
            if (profile == null) {
                Logger.d("No profile found in local storage")
                null
            } else {
                UserProfileDomainModel(
                    uid = profile.uid,
                    name = profile.name,
                    pseudo = profile.pseudo,
                    avatarUrl = profile.avatarUrl,
                    bio = profile.bio,
                    interests = profile.interests,
                    likedMoviesCount = count.toInt(),
                    socialCircle =
                        members.map {
                            UserSummaryDomainModel(
                                uid = it.uid,
                                name = it.name,
                                pseudo = it.pseudo,
                                avatarUrl = it.avatarUrl
                            )
                        }
                )
            }
        }

    override suspend fun syncUserProfile(userProfileDomainModel: UserProfileDomainModel): AppResult<Unit, AppError> {
        return userProfileRemoteDataSource.syncUserProfile(
            userProfileDomainModel.toUserProfileRemoteDataModel()
        )
    }

    override suspend fun upsertUserProfile(userProfileDomainModel: UserProfileDomainModel) =
        userProfileLocalDataSource.upsertUserProfile(userProfileDomainModel.toUserProfileLocalDataModel())

    override suspend fun checkIfPseudoIsAvailable(pseudo: String): AppResult<Boolean, AppError> =
        userProfileRemoteDataSource.checkIfPseudoIsAvailable(pseudo)
}
