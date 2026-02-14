package com.msoula.hobbymatchmaker.core.database.models

data class FavoriteMovieDataEntity(
    val id: Long = 0,
    val title: String = "",
    val posterLocalPath: String = "",
    val releaseDate: String = "",
    val isShared: Boolean = false
) {
    companion object {
        val Initial = FavoriteMovieDataEntity()
    }
}
