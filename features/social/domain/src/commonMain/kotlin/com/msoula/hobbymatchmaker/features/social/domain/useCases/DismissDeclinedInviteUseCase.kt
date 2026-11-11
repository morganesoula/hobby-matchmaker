package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class DismissDeclinedInviteUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(inviteId: String) = socialRepository.dismissDeclinedInvite(inviteId)
}
