package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponseRemoteModel(
    @SerialName("results") val results: List<MovieRemoteModel>? = null
)
