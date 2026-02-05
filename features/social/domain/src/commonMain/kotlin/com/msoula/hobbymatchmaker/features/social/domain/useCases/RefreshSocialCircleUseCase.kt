package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class RefreshSocialCircleUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(uid: String): AppResult<Unit, AppError> = socialRepository.refreshSocialCircle(uid)
}
