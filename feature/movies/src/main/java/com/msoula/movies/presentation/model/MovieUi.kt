package com.msoula.movies.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class MovieUi(
    val id: Long,
    val coverUrl: String,
    val isFavorite: Boolean,
    val playFavoriteAnimation: Boolean,
)
