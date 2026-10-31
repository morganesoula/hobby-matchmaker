package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class BlockUserUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(ownerUid: String, blockedUid: String) =
        socialRepository.blockUser(ownerUid, blockedUid)
}
