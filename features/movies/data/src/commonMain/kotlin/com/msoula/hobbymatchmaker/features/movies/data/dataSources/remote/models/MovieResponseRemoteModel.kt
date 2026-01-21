package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponseRemoteModel(
    @SerialName("page") val page: Int? = null,
    @SerialName("results") val results: List<MovieRemoteModel>? = null,
    @SerialName("total_pages") val totalPages: Int? = null
)
