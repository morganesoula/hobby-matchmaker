package com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.models

import com.google.gson.annotations.SerializedName

data class MovieResponseRemoteModel(
    @SerializedName("results") val results: List<MovieRemoteModel>? = null
)
