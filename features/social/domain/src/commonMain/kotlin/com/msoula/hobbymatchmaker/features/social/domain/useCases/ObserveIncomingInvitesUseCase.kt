package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class ObserveIncomingInvitesUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(ownerUid: String) = socialRepository.observeIncomingInvites(ownerUid)
}
