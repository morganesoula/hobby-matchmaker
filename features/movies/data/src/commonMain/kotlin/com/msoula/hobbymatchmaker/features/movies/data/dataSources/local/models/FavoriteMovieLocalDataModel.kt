package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.models

data class FavoriteMovieLocalDataModel(
    val id: Long = 0,
    val title: String = "",
    val posterPath: String = "",
    val releaseDate: String = ""
) {
    companion object {
        val Initial = FavoriteMovieLocalDataModel()
    }
}
