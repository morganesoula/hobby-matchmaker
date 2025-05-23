package com.msoula.hobbymatchmaker.core.database.models

import com.msoula.hobbymatchmaker.core.database.Actor
import com.msoula.hobbymatchmaker.core.database.Movie

data class MovieDetailDataEntity(
    val movie: Movie,
    val actors: List<Actor>
)
