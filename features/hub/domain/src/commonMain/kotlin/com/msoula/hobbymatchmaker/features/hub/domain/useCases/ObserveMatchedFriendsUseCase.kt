package com.msoula.hobbymatchmaker.features.hub.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.features.hub.domain.models.MatchedFriendDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class ObserveMatchedFriendsUseCase(
    private val socialRepository: SocialRepository,
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(ownerUid: String): AppResult<List<MatchedFriendDomainModel>, AppError> =
        socialRepository.getSocialCircleSnapshot(ownerUid)
            .flatMap { members ->
                movieRepository.getFavoriteLocalMovieIds()
                    .mapSuccess { favoriteMovieIds ->
                        val favoriteSet = favoriteMovieIds.toSet()

                        members.mapNotNull { member ->
                            val sharedMovieIds =
                                member.moviesLiked?.intersect(favoriteSet)?.toList()

                            sharedMovieIds?.let { sharedIds ->
                                if (sharedIds.isNotEmpty()) {
                                    MatchedFriendDomainModel(
                                        uid = member.uid,
                                        displayName = member.name
                                            ?: member.pseudo,
                                        pseudo = member.pseudo,
                                        avatarUrl = member.avatarUrl,
                                        sharedMovieIds = sharedMovieIds
                                    )
                                } else null
                            }
                        }
                    }
            }
}
