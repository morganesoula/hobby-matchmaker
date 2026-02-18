package com.msoula.hobbymatchmaker.features.hub.presentation.mappers

import com.msoula.hobbymatchmaker.core.design.models.MovieCarouselItem
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
import com.msoula.hobbymatchmaker.features.hub.domain.models.MatchedFriendDomainModel
import com.msoula.hobbymatchmaker.features.hub.presentation.models.HubFavoriteMoviesUIModel
import com.msoula.hobbymatchmaker.features.hub.presentation.models.HubRecentMatchesUIModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.FavoriteMovieDomainModel

fun FavoriteMovieDomainModel.toHubFavoriteMoviesUIModel(isShared: Boolean): HubFavoriteMoviesUIModel =
    HubFavoriteMoviesUIModel(
        id = this.id,
        title = this.title,
        releaseDate = this.releaseDate,
        posterPath = this.posterPath,
        isShared = isShared
    )

fun HubFavoriteMoviesUIModel.toMovieCarouselItem(): MovieCarouselItem =
    MovieCarouselItem(
        id = this.id,
        title = this.title,
        overview = "",
        coverFilePath = this.posterPath,
        releaseDate = this.releaseDate,
        note = 0.0,
        isFavorite = false,
        isShared = this.isShared
    )

fun HubRecentMatchesUIModel.toProfileSocialMember(): ProfileSocialMember =
    ProfileSocialMember(
        uid = "",
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        commonMoviesCount = this.sharedWithCount
    )

fun MatchedFriendDomainModel.toHubRecentMatchesUIModel(): HubRecentMatchesUIModel =
    HubRecentMatchesUIModel(
        name = this.displayName,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        sharedMovieIds = this.sharedMovieIds,
        sharedWithCount = this.commonMoviesCount
    )
