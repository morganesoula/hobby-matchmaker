package com.msoula.hobbymatchmaker.features.hub.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.common.toFirebaseError
import com.msoula.hobbymatchmaker.features.hub.domain.models.MatchedFriendDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest

class ObserveMatchedFriendsUseCase(
    private val socialRepository: SocialRepository,
    private val movieRepository: MovieRepository
) {
    /**
     * Re-computes matches whenever the social circle (incl. match timestamps) or the user's
     * favorites change. Most recent match first; members without a known match date come last.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(ownerUid: String): Flow<AppResult<List<MatchedFriendDomainModel>, AppError>> =
        combine(
            socialRepository.observeSocialCircle(ownerUid),
            movieRepository.observeLikedMoviesIds()
        ) { circle, favoriteMovieIds ->
            circle.associate { it.uid to it.lastMatchAt } to favoriteMovieIds.toSet()
        }
            .mapLatest { (lastMatchAtByUid, favoriteSet) ->
                socialRepository.getSocialCircleSnapshot(ownerUid)
                    .mapSuccess { members ->
                        members.mapNotNull { member ->
                            val sharedMovieIds = member.moviesLiked
                                ?.filter { it in favoriteSet }
                                ?.takeIf { it.isNotEmpty() }
                                ?: return@mapNotNull null

                            lastMatchAtByUid[member.uid] to MatchedFriendDomainModel(
                                uid = member.uid,
                                displayName = member.name.takeIf { it?.isNotBlank() == true }
                                    ?: member.pseudo,
                                pseudo = member.pseudo,
                                avatarUrl = member.avatarUrl,
                                sharedMovieIds = sharedMovieIds
                            )
                        }
                            .sortedWith(
                                compareByDescending<Pair<Long?, MatchedFriendDomainModel>> {
                                    it.first ?: Long.MIN_VALUE
                                }.thenByDescending { it.second.sharedMovieIds.size }
                            )
                            .map { it.second }
                    }
            }
            .catch { throwable -> emit(AppResult.Failure(throwable.toFirebaseError())) }
}
