package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class RefreshIncomingInvitesUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(ownerUid: String): AppResult<Unit, AppError> =
        socialRepository.refreshIncomingInvites(ownerUid)
}
