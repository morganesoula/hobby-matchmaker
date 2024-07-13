package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.models

import com.google.gson.annotations.SerializedName

data class MovieDetailResponseRemoteModel(
    @SerializedName("genres") val genres: List<GenreRemoteModel> = emptyList(),
    @SerializedName("original_title") val originalTitle: String = "",
    @SerializedName("overview") val overview: String,
    @SerializedName("popularity") val popularity: Double = -1.0,
    @SerializedName("release_date") val releaseDate: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("title") val title: String = ""
)

data class GenreRemoteModel(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null
)
