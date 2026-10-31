package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models

data class SocialCircleEntryRemoteDataModel(
    val memberUid: String = "",
    val avatarUrl: String = "",
    val memberPseudo: String? = null,
    val memberName: String? = null,
    val commonMoviesCount: Int = 0,
    val moviesLiked: List<Long>? = null,
    val lastMatchAt: Long? = null
)
