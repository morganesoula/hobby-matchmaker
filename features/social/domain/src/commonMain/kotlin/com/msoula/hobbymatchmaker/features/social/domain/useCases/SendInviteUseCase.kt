package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class SendInviteUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(invite: SocialInviteDomainModel) =
        socialRepository.sendInvite(invite)
}
