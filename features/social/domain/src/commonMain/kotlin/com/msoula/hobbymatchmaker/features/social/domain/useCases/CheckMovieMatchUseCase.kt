package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.domain.models.MatchingMemberDomainModel
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
                val matchingMemberDomainModels = result.data
                    .filter { SocialMatchingUtils.hasMoviesInCommon(movieId, it.moviesLiked) }
                    .mapNotNull { member ->
                        val displayName =
                            member.pseudo.ifBlank { member.name } ?: return@mapNotNull null
                        if (displayName.isBlank()) return@mapNotNull null
                        MatchingMemberDomainModel(
                            displayName = displayName,
                            avatarUrl = member.avatarUrl
                        )
                    }

                if (matchingMemberDomainModels.isEmpty()) {
                    AppResult.Success(MovieMatchResult.NoMatch)
                } else {
                    AppResult.Success(MovieMatchResult.Match(matchingMemberDomainModels))
                }
            }

            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }
}
