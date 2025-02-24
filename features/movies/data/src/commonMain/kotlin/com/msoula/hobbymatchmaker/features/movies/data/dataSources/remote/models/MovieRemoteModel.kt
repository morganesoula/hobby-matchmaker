package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieRemoteModel(
    @SerialName("id") val id: Int? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("poster_path") val poster: String? = null
)
