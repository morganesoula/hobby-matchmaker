package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import com.msoula.hobbymatchmaker.features.social.domain.utils.SocialMatchingUtils

class AcceptInviteUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(
        inviteId: String,
        ownerUid: String,
        guestUid: String
    ): AppResult<Unit, AppError> {
        return socialRepository.findUserByUid(ownerUid)
            .flatMap { owner ->
                socialRepository.findUserByUid(guestUid)
                    .flatMap { member ->
                        val commonMoviesCount = SocialMatchingUtils.calculateCommonMoviesCount(
                            owner.moviesLiked, member.moviesLiked
                        )

                        val ownerWithCount = owner.copy(commonMoviesCount = commonMoviesCount)
                        val memberWithCount = member.copy(commonMoviesCount = commonMoviesCount)

                        socialRepository.acceptInviteAndAddMembers(
                            inviteId = inviteId,
                            owner = ownerWithCount,
                            member = memberWithCount
                        )
                    }
            }
    }
}
