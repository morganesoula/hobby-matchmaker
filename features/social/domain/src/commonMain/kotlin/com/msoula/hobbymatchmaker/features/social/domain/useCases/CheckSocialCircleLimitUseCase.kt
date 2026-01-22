package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class CheckSocialCircleLimitUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(ownerUid: String, invitingMemberUid: String) =
        socialRepository.checkSocialCircleLimit(ownerUid, invitingMemberUid)
}
