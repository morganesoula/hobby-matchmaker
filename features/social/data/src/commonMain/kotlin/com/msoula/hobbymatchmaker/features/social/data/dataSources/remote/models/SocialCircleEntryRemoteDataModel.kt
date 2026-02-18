package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models

data class SocialCircleEntryRemoteDataModel(
    val memberUid: String = "",
    val avatarUrl: String = "",
    val memberPseudo: String = "",
    val memberName: String = "",
    val commonMoviesCount: Int = 0,
    val moviesLiked: List<Long>? = null
)
