package com.msoula.hobbymatchmaker.features.movies.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class MovieUiModel(
    val id: Long,
    val coverFilePath: String,
    val isFavorite: Boolean,
    val playFavoriteAnimation: Boolean = true,
    val title: String,
    val overview: String?
)
