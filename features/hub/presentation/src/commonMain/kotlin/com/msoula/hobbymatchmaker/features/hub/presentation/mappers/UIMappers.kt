package com.msoula.hobbymatchmaker.features.hub.presentation.mappers

import com.msoula.hobbymatchmaker.core.design.models.MovieCarouselItem
import com.msoula.hobbymatchmaker.features.hub.presentation.models.HubFavoriteMoviesUIModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.FavoriteMovieDomainModel

fun FavoriteMovieDomainModel.toHubFavoriteMoviesUIModel(): HubFavoriteMoviesUIModel =
    HubFavoriteMoviesUIModel(
        id = this.id,
        title = this.title,
        releaseDate = this.releaseDate,
        posterPath = this.posterPath
    )

fun HubFavoriteMoviesUIModel.toMovieCarouselItem(): MovieCarouselItem =
    MovieCarouselItem(
        id = this.id,
        title = this.title,
        overview = "",
        coverFilePath = this.posterPath,
        releaseDate = this.releaseDate,
        note = 0.0,
        isFavorite = false
    )
