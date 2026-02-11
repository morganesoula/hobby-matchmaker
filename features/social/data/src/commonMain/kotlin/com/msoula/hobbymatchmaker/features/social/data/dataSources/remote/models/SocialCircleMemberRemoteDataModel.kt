package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models

data class SocialCircleMemberRemoteDataModel(
    val uid: String = "",
    val ownerUid: String = "",
    val pseudo: String = "",
    val name: String? = "",
    val avatarUrl: String? = "",
    val moviesLiked: List<Long>? = emptyList(),
    val commonMoviesCount: Int? = 0
) {
    companion object Companion {
        val Initial = SocialCircleMemberRemoteDataModel()
    }
}
