package com.msoula.hobbymatchmaker.features.profile.domain.useCases

import com.msoula.hobbymatchmaker.features.profile.domain.repositories.SocialRepository

class InviteToSocialCircleUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(currentUserUid: String?, pseudo: String) =
        socialRepository.inviteToCircle(currentUserUid, pseudo)
}
