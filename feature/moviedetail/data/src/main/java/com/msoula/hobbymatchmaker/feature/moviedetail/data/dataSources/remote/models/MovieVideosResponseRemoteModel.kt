package com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.models

import com.google.gson.annotations.SerializedName

data class MovieVideosResponseRemoteModel(
    @SerializedName("id") val id: Int = -1,
    @SerializedName("results") val results: List<MovieVideoResponseRemoteModel> = emptyList()
)

data class MovieVideoResponseRemoteModel(
    @SerializedName("type") val type: String,
    @SerializedName("key") val key: String,
    @SerializedName("site") val site: String
) {
    companion object {
        const val DEFAULT_NAME: String = ""
        const val DEFAULT_KEY: String = ""
        const val DEFAULT_SITE: String = ""
    }
}
