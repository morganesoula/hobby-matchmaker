package com.msoula.movies.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class MovieUi(
    val id: Int,
    val coverUrl: String,
    val isFavorite: Boolean,
)
