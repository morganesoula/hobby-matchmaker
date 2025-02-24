package com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieVideosResponseRemoteModel(
    @SerialName("id") val id: Int = -1,
    @SerialName("results") val results: List<MovieVideoResponseRemoteModel> = emptyList()
)

@Serializable
data class MovieVideoResponseRemoteModel(
    @SerialName("type") val type: String,
    @SerialName("key") val key: String,
    @SerialName("site") val site: String
) {
    companion object {
        const val DEFAULT_NAME: String = ""
        const val DEFAULT_KEY: String = ""
        const val DEFAULT_SITE: String = ""
    }
}
