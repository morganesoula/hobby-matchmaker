package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class AcceptInviteUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(
        inviteId: String,
        ownerUid: String,
        guestUid: String
    ): AppResult<Unit, AppError> {
        return socialRepository.findUserByUid(guestUid)
            .flatMap { member ->
                member ?: return@flatMap AppResult.Failure(AppError.Domain.NotFound)
                socialRepository.addMember(ownerUid, member)
            }
            .flatMap {
                socialRepository.acceptInvite(inviteId)
            }
    }
}
