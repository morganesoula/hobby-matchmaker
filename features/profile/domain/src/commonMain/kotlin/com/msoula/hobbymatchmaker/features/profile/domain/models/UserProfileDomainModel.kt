package com.msoula.hobbymatchmaker.features.profile.domain.models

data class UserProfileDomainModel(
    val uid: String,
    val name: String,
    val pseudo: String,
    val avatarUrl: String?,
    val bio: String?,
    val interests: List<String>,
    val likedMoviesCount: Int,
    val socialCircle: List<UserSummaryDomainModel>
) {
    companion object {
        const val DEFAULT_UID: String = ""
        const val DEFAULT_NAME = ""
        const val DEFAULT_PSEUDO = ""
        const val DEFAULT_AVATAR_URL = ""
        const val DEFAULT_BIO = ""
        val DEFAULT_INTERESTS: List<String> = emptyList()
        const val DEFAULT_LIKED_MOVIES_COUNT = 0
        val DEFAULT_SOCIAL_CIRCLE: List<UserSummaryDomainModel> = emptyList()

        fun empty(): UserProfileDomainModel = UserProfileDomainModel(
            uid = DEFAULT_UID,
            name = DEFAULT_NAME,
            pseudo = DEFAULT_PSEUDO,
            avatarUrl = DEFAULT_AVATAR_URL,
            bio = DEFAULT_BIO,
            interests = DEFAULT_INTERESTS,
            likedMoviesCount = DEFAULT_LIKED_MOVIES_COUNT,
            socialCircle = DEFAULT_SOCIAL_CIRCLE
        )
    }
}

data class UserSummaryDomainModel(
    val uid: String,
    val name: String?,
    val pseudo: String,
    val avatarUrl: String?
) {
    companion object {
        const val DEFAULT_UID: String = ""
        const val DEFAULT_NAME: String = ""
        const val DEFAULT_PSEUDO: String = ""
        const val DEFAULT_AVATAR_URL: String = ""
    }
}
