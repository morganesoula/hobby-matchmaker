package com.msoula.hobbymatchmaker.features.social.domain.models

data class SocialMemberDomainModel(
    val uid: String = "",
    val pseudo: String = "",
    val name: String? = "",
    val avatarUrl: String? = "",
    val moviesLiked: List<Long>? = emptyList(),
    val commonMoviesCount: Int = 0
) {
    companion object {
        val Initial = SocialMemberDomainModel()
    }
}
