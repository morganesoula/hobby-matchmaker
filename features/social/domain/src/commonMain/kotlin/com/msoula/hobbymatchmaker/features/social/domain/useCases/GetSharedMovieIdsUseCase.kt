package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class GetSharedMovieIdsUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(ownerUid: String): AppResult<Set<Long>, AppError> {
        return when (val result = socialRepository.getSocialCircleSnapshot(ownerUid)) {
            is AppResult.Success -> {
                val sharedIds = result.data
                    .flatMap { it.moviesLiked.orEmpty() }
                    .toSet()
                AppResult.Success(sharedIds)
            }

            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }
}
