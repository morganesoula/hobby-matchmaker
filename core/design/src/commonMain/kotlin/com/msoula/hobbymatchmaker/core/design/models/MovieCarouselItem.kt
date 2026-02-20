package com.msoula.hobbymatchmaker.core.design.models

import androidx.compose.runtime.Immutable

@Immutable
data class MovieCarouselItem(
    val id: Long,
    val title: String,
    val overview: String,
    val coverFilePath: String,
    val releaseDate: String,
    val note: Double,
    val isFavorite: Boolean,
    val isShared: Boolean
)
