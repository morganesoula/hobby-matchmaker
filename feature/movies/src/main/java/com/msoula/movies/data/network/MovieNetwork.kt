package com.msoula.movies.data.network

import com.google.gson.annotations.SerializedName

data class MovieNetwork(
    @SerializedName("results") val results: List<MoviePogo>,
)

data class MoviePogo(
    @SerializedName("id") val id: Int,
    @SerializedName("original_title") val title: String,
    @SerializedName("poster_path") val poster: String,
)
