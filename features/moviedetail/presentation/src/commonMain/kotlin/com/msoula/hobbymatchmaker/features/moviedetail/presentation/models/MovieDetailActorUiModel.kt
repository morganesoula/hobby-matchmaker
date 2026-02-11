package com.msoula.hobbymatchmaker.features.moviedetail.presentation.models

data class MovieDetailActorUiModel(
    val name: String = "",
    val role: String = ""
) {
    companion object {
        val Initial = MovieDetailActorUiModel()
    }
}
