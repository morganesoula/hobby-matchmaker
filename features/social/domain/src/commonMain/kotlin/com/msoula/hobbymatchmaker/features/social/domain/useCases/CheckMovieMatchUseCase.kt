package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.domain.models.MovieMatchResult
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import com.msoula.hobbymatchmaker.features.social.domain.utils.SocialMatchingUtils

class CheckMovieMatchUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(
        ownerUid: String,
        movieId: Long
    ): AppResult<MovieMatchResult, AppError> {
        return when (val result = socialRepository.getSocialCircleSnapshot(ownerUid)) {
            is AppResult.Success -> {
                val matchingNames = result.data
                    .filter { member ->
                        SocialMatchingUtils.hasMoviesInCommon(movieId, member.moviesLiked)
                    }
                    .mapNotNull { member ->
                        member.pseudo.ifBlank { member.name }
                    }
                    .filter { it.isNotBlank() }

                if (matchingNames.isEmpty()) {
                    AppResult.Success(MovieMatchResult.NoMatch)
                } else {
                    AppResult.Success(MovieMatchResult.Match(matchingNames))
                }
            }

            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }
}
