package com.msoula.movies.data.model

import androidx.compose.runtime.toMutableStateList
import com.msoula.movies.presentation.model.MovieUi

data class Movie(
    val id: Long,
    val title: String,
    val posterJPG: String,
    val localPosterPath: String,
    val isFavorite: Boolean,
    val seen: Boolean,
)

fun Movie.toMovieUI(): MovieUi =
    MovieUi(
        id = this.id,
        coverUrl = this.localPosterPath,
        isFavorite = this.isFavorite,
        playFavoriteAnimation = true,
    )

fun List<Movie>.toListMovieUI(): MutableList<MovieUi> =
    this.map { it.toMovieUI() }.toMutableStateList()
