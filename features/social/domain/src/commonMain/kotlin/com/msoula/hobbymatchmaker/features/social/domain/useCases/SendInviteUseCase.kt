package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class SendInviteUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(fromUid: String, toPseudo: String) =
        socialRepository.sendInvite(fromUid, toPseudo)
}
