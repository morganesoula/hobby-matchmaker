package com.msoula.hobbymatchmaker.features.social.domain.models

data class SocialUserSummaryDomainModel(
    val uid: String = "",
    val name: String? = "",
    val pseudo: String = "",
    val avatarUrl: String? = "",
    val moviesLiked: List<Long>? = emptyList()
) {
    companion object {
        val Initial = SocialUserSummaryDomainModel()
    }
}
