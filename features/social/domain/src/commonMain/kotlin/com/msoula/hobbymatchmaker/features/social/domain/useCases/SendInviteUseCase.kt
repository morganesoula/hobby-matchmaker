package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import kotlin.time.Clock

class SendInviteUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(
        uid: String,
        ownerPseudo: String?,
        invitePseudo: String,
        name: String?
    ): AppResult<Unit, AppError> {
        // Invites are matched by pseudo: without one the receiver could never see who sent it
        if (ownerPseudo.isNullOrBlank()) return AppResult.Failure(AppError.Profile.PseudoMissing)

        return socialRepository.sendInvite(
            SocialInviteDomainModel(
                inviteId = "",
                fromUid = uid,
                fromPseudo = ownerPseudo,
                toPseudo = invitePseudo,
                name = name,
                status = InviteStatus.PENDING,
                createdAt = Clock.System.now(),
                updatedAt = null
            )
        )
    }
}
