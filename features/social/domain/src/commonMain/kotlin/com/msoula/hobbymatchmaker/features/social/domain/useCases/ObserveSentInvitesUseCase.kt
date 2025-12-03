package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class ObserveSentInvitesUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(ownerUid: String) = socialRepository.observeSentInvites(ownerUid)
}
