package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.models

data class UpdatedMovieDetailDataModel(
    val id: Long = 0L,
    val releaseDate: String? = null,
    val overview: String? = null,
    val genres: String? = null,
    val status: String? = null,
    val popularity: Double? = 0.0,
    val cast: List<ActorDataModel> = emptyList(),
    val duration: Int? = null,
    val hasCast: Boolean = false
) {
    companion object {
        val Initial = UpdatedMovieDetailDataModel()
    }
}
