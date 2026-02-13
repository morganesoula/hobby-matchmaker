package com.msoula.hobbymatchmaker.features.profile.presentation.interactors

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ImageFileManager
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveLikedMoviesCountUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveLikedMoviesIdsUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.IsPseudoAvailableUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.SyncUserProfileUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.UpsertUserProfileUseCase
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toUserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveSocialCircleUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.RefreshSocialCircleUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class UserProfileInteractor(
    private val observeCurrentUserProfileState: ObserveCurrentUserProfileStateUseCase,
    private val observeSocialCircle: ObserveSocialCircleUseCase,
    private val observeLikedMoviesCount: ObserveLikedMoviesCountUseCase,
    private val observeLikedMoviesIds: ObserveLikedMoviesIdsUseCase,
    private val checkPseudo: IsPseudoAvailableUseCase,
    private val upsertUser: UpsertUserProfileUseCase,
    private val syncUser: SyncUserProfileUseCase,
    private val imageManager: ImageFileManager,
    private val logOutUseCase: LogOutUseCase,
    private val refreshSocialCircleUseCase: RefreshSocialCircleUseCase
) {
    fun observeCurrentUser(uid: String): Flow<UserProfileDomainModel?> =
        combine(
            observeCurrentUserProfileState(uid),
            observeLikedMoviesCount(),
            observeLikedMoviesIds(),
            observeSocialCircle(uid)
                .onStart { emit(emptyList()) }
        ) { profile, count, ids, members ->
            val userMoviesSet = ids.toSet()

            UserProfileDomainModel(
                uid = profile.uid,
                name = profile.name,
                pseudo = profile.pseudo,
                avatarUrl = profile.avatarUrl,
                bio = profile.bio,
                interests = profile.interests,
                likedMoviesCount = count.toInt(),
                socialCircle =
                    members.map { member ->
                        val commonCount = member.moviesLiked
                            ?.count { movieId -> userMoviesSet.contains(movieId) }
                            ?: 0

                        UserSummaryDomainModel(
                            uid = member.uid,
                            name = member.name ?: UserSummaryDomainModel.Initial.name,
                            pseudo = member.pseudo,
                            avatarUrl = member.avatarUrl,
                            commonMoviesCount = commonCount
                        )
                    }
            )
        }

    suspend fun checkPseudoAvailable(pseudo: String): AppResult<Boolean, AppError> {
        return checkPseudo(pseudo)
    }


    suspend fun saveProfile(uid: String, profile: UserProfileUiModel) =
        upsertUser(profile.toUserProfileDomainModel(uid))

    suspend fun syncProfile(uid: String, profile: UserProfileUiModel) =
        syncUser(profile.toUserProfileDomainModel(uid))

    @OptIn(ExperimentalTime::class)
    suspend fun saveAvatar(
        uid: String,
        oldPath: String?,
        newPath: String
    ): AppResult<String, AppError> {
        val fileName = "avatar_${uid}_${Clock.System.now()}.jpg"

        val internalPath =
            imageManager.copyImageToInternalStorage(newPath, fileName)
                ?: return AppResult.Failure(AppError.Storage.WriteFailed)

        if (oldPath != null &&
            (oldPath.startsWith("/data/") ||
                oldPath.contains("/files/avatars"))
        ) {
            imageManager.deleteImageFromInternalStorage(oldPath)
        }

        return AppResult.Success(internalPath)
    }

    suspend fun logOut() = logOutUseCase()

    suspend fun refreshProfileData(uid: String) = refreshSocialCircleUseCase(uid)
}
