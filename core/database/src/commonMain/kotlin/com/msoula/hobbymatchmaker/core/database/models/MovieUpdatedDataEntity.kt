package com.msoula.hobbymatchmaker.core.database.models

import com.msoula.hobbymatchmaker.core.database.Actor

data class MovieUpdatedDataEntity(
    val movieId: Long,
    val releaseDate: String?,
    val overview: String?,
    val genres: String?,
    val status: String?,
    val popularity: Double?,
    val cast: List<Actor>
)
