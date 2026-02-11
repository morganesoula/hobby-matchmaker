package com.msoula.hobbymatchmaker.features.moviedetail.domain.models

data class MovieVideoDomainModel(
    val key: String = "",
    val type: String = "",
    val site: String = ""
) {
    companion object {
        val Initial = MovieVideoDomainModel()
    }
}
