package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import kotlin.time.Clock

class SendInviteUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(uid: String, ownerPseudo: String?, invitePseudo: String, name: String?) =
        socialRepository.sendInvite(
            SocialInviteDomainModel(
                inviteId = "",
                fromUid = uid,
                fromPseudo = ownerPseudo ?: "",
                toPseudo = invitePseudo,
                name = name,
                status = InviteStatus.PENDING,
                createdAt = Clock.System.now(),
                updatedAt = null
            )
        )
}
