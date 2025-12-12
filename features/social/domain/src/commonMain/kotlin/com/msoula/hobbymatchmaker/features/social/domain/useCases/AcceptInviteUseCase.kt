package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class AcceptInviteUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(
        inviteId: String,
        ownerUid: String,
        guestUid: String
    ) = socialRepository.acceptInvite(inviteId, ownerUid, guestUid)
}
