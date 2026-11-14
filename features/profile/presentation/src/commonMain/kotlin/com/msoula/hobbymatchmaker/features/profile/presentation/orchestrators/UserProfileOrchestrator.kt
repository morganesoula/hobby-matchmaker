package com.msoula.hobbymatchmaker.features.profile.presentation.orchestrators

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ImageFileManager
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveFavoriteMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveLikedMoviesCountUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.UploadAvatarUseCase
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toMovieCarouselItem
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toUserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// The profile only previews the latest favorites; the full list lives in the Hub
private const val MAX_PROFILE_FAVORITES = 8

class UserProfileOrchestrator(
    private val observeCurrentUserProfileState: ObserveCurrentUserProfileStateUseCase,
    private val observeFavoriteMovies: ObserveFavoriteMoviesUseCase,
    private val observeLikedMoviesCount: ObserveLikedMoviesCountUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val imageManager: ImageFileManager
) {
    fun observeCurrentUser(uid: String): Flow<UserProfileUiModel?> =
        combine(
            observeCurrentUserProfileState(uid),
            observeLikedMoviesCount(),
            observeFavoriteMovies()
        ) { profile, count, favorites ->
            profile.toUserProfileUiModel(
                likedMoviesCount = count.toInt(),
                favorites = favorites.take(MAX_PROFILE_FAVORITES).map { it.toMovieCarouselItem() }
            )
        }

    @OptIn(ExperimentalTime::class)
    suspend fun saveAvatar(
        uid: String,
        oldPath: String?,
        newPath: String
    ): AppResult<String, AppError> {
        val fileName = "avatar_${uid}_${Clock.System.now()}.jpg"
        val localPath = imageManager.copyImageToInternalStorage(newPath, fileName)
            ?: return AppResult.Failure(AppError.Storage.WriteFailed)

        return when (val result = uploadAvatarUseCase(uid, localPath)) {
            is AppResult.Failure -> {
                Logger.e("Error in savingAvatar: ${result.error}")
                result
            }

            is AppResult.Success -> {
                deleteLocalAvatar(oldPath)
                AppResult.Success(result.data)
            }
        }
    }

    private fun deleteLocalAvatar(path: String?) {
        if (path != null && (path.startsWith("/data") || path.contains("/files/avatars"))) {
            imageManager.deleteImageFromInternalStorage(path)
        }
    }
}
