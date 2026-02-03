package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class GetUserAvatarUrlUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(uid: String): AppResult<String?, AppError> {
        return when (val result = socialRepository.findUserByUid(uid)) {
            is AppResult.Success -> AppResult.Success(result.data?.avatarUrl)
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }
}
