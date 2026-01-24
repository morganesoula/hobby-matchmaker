package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

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
                owner ?: return@flatMap AppResult.Failure(AppError.Domain.NotFound)
                socialRepository.findUserByUid(guestUid)
                    .flatMap { member ->
                        member ?: return@flatMap AppResult.Failure(AppError.Domain.NotFound)

                        val commonMoviesCount = calculateCommonMovies(
                            owner.moviesLiked,
                            member.moviesLiked
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

    private fun calculateCommonMovies(
        ownerMovies: List<Long>?,
        memberMovies: List<Long>?
    ): Int {
        if (ownerMovies.isNullOrEmpty() || memberMovies.isNullOrEmpty()) return 0
        return ownerMovies.intersect(memberMovies.toSet()).size
    }
}
