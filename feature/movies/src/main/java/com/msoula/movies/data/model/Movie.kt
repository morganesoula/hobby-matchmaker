package com.msoula.movies.data.model

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

data class Movie(
    val id: Int = -1,
    val title: String = "",
    val posterJPG: String = "",
    val localPosterPath: String = "",
    val isFavorite: Boolean = false,
    val seen: Boolean = false
)

fun Movie.toMovieUI(): MovieUi =
    MovieUi(id = this.id, coverUrl = this.localPosterPath, isFavorite = this.isFavorite)

fun List<Movie>.toListMovieUI(): SnapshotStateList<MovieUi> =
    this.map { it.toMovieUI() }.toMutableStateList()
