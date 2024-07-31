package com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.models

import com.google.gson.annotations.SerializedName

data class MovieRemoteModel(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("poster_path") val poster: String? = null
)
