package com.msoula.hobbymatchmaker.features.profile.domain.models

data class UserProfileDomainModel(
    val uid: Long,
    val name: String,
    val avatarUrl: String?,
    val bio: String?,
    val interests: List<String>?,
    val likedMoviesCount: Int,
    val socialCircle: List<UserSummaryDomainModel>?
) {
    companion object {
        const val DEFAULT_UID: Long = -1
        const val DEFAULT_NAME = ""
        const val DEFAULT_AVATAR_URL = ""
        const val DEFAULT_BIO = ""
        val DEFAULT_INTERESTS = emptyList<String>()
        const val DEFAULT_LIKED_MOVIES_COUNT = 0
        val DEFAULT_SOCIAL_CIRCLE = emptyList<UserSummaryDomainModel>()
    }
}

data class UserSummaryDomainModel(
    val uid: Long,
    val name: String,
    val avatarUrl: String,
    val likedMoviesCount: Int
) {
    companion object {
        const val DEFAULT_UID: Long = -1
        const val DEFAULT_NAME: String = ""
        const val DEFAULT_AVATAR_URL: String = ""
        const val DEFAULT_LIKED_MOVIES_COUNT: Int = 0
    }
}
