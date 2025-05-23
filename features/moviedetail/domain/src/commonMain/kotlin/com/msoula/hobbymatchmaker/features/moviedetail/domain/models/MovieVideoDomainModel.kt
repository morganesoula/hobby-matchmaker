package com.msoula.hobbymatchmaker.features.moviedetail.domain.models

data class MovieVideoDomainModel(
    val key: String,
    val type: String,
    val site: String
) {
    companion object {
        const val DEFAULT_KEY: String = ""
        const val DEFAULT_TYPE: String = ""
        const val DEFAULT_SITE: String = ""
    }
}
