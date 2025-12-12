package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models

data class SocialCircleMember(
    val uid: String,
    val ownerUid: String,
    val pseudo: String,
    val name: String?,
    val avatarUrl: String?,
    val commonMovieCount: Int?
)
