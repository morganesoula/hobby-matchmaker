package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class RemoveMemberUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(ownerUid: String, memberUid: String) =
        socialRepository.removeMember(ownerUid, memberUid)
}
