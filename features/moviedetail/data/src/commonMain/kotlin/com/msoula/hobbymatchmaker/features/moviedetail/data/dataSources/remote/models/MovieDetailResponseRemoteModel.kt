package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailResponseRemoteModel(
    @SerialName("id") val id: Int = -1,
    @SerialName("genres") val genres: List<GenreResponseRemoteModel> = emptyList(),
    @SerialName("original_title") val originalTitle: String = "",
    @SerialName("overview") val overview: String,
    @SerialName("popularity") val popularity: Double = -1.0,
    @SerialName("release_date") val releaseDate: String = "",
    @SerialName("status") val status: String = "",
    @SerialName("title") val title: String = ""
)

@Serializable
data class GenreResponseRemoteModel(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null
)

@Serializable
data class CastResponseRemoteModel(
    @SerialName("cast") val cast: List<ActorResponseRemoteModel>? = null
)

@Serializable
data class ActorResponseRemoteModel(
    @SerialName("id") val id: Int = -1,
    @SerialName("name") val name: String = "",
    @SerialName("character") val character: String = ""
)
