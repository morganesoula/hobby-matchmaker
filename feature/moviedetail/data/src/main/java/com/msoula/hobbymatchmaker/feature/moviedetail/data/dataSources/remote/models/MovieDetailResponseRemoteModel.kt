package com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.models

import com.google.gson.annotations.SerializedName

data class MovieDetailResponseRemoteModel(
    @SerializedName("id") val id: Int = -1,
    @SerializedName("genres") val genres: List<GenreResponseRemoteModel> = emptyList(),
    @SerializedName("original_title") val originalTitle: String = "",
    @SerializedName("overview") val overview: String,
    @SerializedName("popularity") val popularity: Double = -1.0,
    @SerializedName("release_date") val releaseDate: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("title") val title: String = ""
)

data class GenreResponseRemoteModel(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null
)

data class CastResponseRemoteModel(
    @SerializedName("cast") val cast: List<ActorResponseRemoteModel>? = null
)

data class ActorResponseRemoteModel(
    @SerializedName("id") val id: Int = -1,
    @SerializedName("name") val name: String = "",
    @SerializedName("character") val character: String = ""
)
