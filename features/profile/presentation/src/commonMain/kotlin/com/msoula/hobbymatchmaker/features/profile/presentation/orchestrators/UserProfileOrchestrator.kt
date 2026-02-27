package com.msoula.hobbymatchmaker.features.profile.presentation.orchestrators

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ImageFileManager
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveLikedMoviesCountUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveLikedMoviesIdsUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileStateUseCase
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toSocialMemberUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toUserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ComputeCommonMoviesUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveSocialCircleUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class UserProfileOrchestrator(
    private val observeCurrentUserProfileState: ObserveCurrentUserProfileStateUseCase,
    private val observeSocialCircle: ObserveSocialCircleUseCase,
    private val observeLikedMoviesCount: ObserveLikedMoviesCountUseCase,
    private val observeLikedMoviesIds: ObserveLikedMoviesIdsUseCase,
    private val computeCommonMoviesUseCase: ComputeCommonMoviesUseCase,
    private val imageManager: ImageFileManager
) {
    fun observeCurrentUser(uid: String): Flow<UserProfileUiModel?> =
        combine(
            observeCurrentUserProfileState(uid),
            observeLikedMoviesCount(),
            observeLikedMoviesIds(),
            observeSocialCircle(uid)
                .onStart { emit(emptyList()) }
        ) { profile, count, ids, members ->
            val userMoviesSet = ids.toSet()
            profile.toUserProfileUiModel(
                likedMoviesCount = count.toInt(),
                socialCircle = members.map { member ->
                    member.toSocialMemberUiModel(
                        commonMoviesCount = computeCommonMoviesUseCase(
                            member.moviesLiked ?: emptyList(),
                            userMoviesSet
                        )
                    )
                }
            )
        }

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
}
