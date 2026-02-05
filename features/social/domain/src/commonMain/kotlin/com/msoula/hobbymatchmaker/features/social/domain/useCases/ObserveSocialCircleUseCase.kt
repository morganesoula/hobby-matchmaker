package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class ObserveSocialCircleUseCase(
    private val socialRepository: SocialRepository
) {
    operator fun invoke(ownerUid: String) = socialRepository.observeSocialCircle(ownerUid)
}
