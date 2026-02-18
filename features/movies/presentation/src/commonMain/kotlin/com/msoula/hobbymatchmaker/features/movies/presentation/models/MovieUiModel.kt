package com.msoula.hobbymatchmaker.features.movies.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class MovieUiModel(
    val id: Long = 0,
    val coverFilePath: String = "",
    val isFavorite: Boolean = false,
    val releaseDate: String = "",
    val playFavoriteAnimation: Boolean = true,
    val title: String = "",
    val overview: String = "",
    val note: Double = 0.0,
    val isShared: Boolean = false
) {
    companion object {
        val Initial = MovieUiModel()
    }
}
