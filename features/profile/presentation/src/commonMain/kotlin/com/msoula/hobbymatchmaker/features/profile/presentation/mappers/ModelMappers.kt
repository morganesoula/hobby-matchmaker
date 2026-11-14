package com.msoula.hobbymatchmaker.features.profile.presentation.mappers

import com.msoula.hobbymatchmaker.core.design.models.MovieCarouselItem
import com.msoula.hobbymatchmaker.features.movies.domain.models.FavoriteMovieDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileNoCircleDomainModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel

fun UserProfileUiModel.toUserProfileDomainModel(uid: String): UserProfileDomainModel =
    UserProfileDomainModel(
        uid = uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        bio = this.bio,
        interests = this.interests ?: emptyList(),
        likedMoviesCount = this.moviesLikedCount
    )

fun UserProfileNoCircleDomainModel.toUserProfileUiModel(
    likedMoviesCount: Int,
    favorites: List<MovieCarouselItem>
): UserProfileUiModel =
    UserProfileUiModel(
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        bio = this.bio,
        interests = this.interests,
        moviesLikedCount = likedMoviesCount,
        favorites = favorites
    )

fun FavoriteMovieDomainModel.toMovieCarouselItem(): MovieCarouselItem =
    MovieCarouselItem(
        id = id,
        title = title,
        overview = "",
        coverFilePath = posterPath,
        releaseDate = "",
        note = 0.0,
        isFavorite = true,
        isShared = false
    )
