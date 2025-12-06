package com.msoula.hobbymatchmaker.core.design.models

data class MovieCarouselItem(
    val id: Long,
    val title: String,
    val overview: String,
    val coverFilePath: String,
    val note: Double,
    val isFavorite: Boolean
)
