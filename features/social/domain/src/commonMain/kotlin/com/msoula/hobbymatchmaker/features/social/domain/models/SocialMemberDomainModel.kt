package com.msoula.hobbymatchmaker.features.social.domain.models

data class SocialMemberDomainModel(
    val uid: String,
    val pseudo: String,
    val name: String?,
    val avatarUrl: String?,
    val commonMoviesCount: Int?
) {
    companion object {
        const val DEFAULT_PSEUDO = ""
        const val DEFAULT_NAME = ""
        const val DEFAULT_AVATAR_URL = ""
        const val DEFAULT_COMMON_MOVIES_COUNT = 0
    }
}
