package com.msoula.hobbymatchmaker.features.profile.domain.models

data class UserProfileDomainModel(
    val uid: String = "",
    val name: String = "",
    val pseudo: String = "",
    val avatarUrl: String? = "",
    val bio: String? = "",
    val interests: List<String> = emptyList(),
    val likedMoviesCount: Int = 0,
    val socialCircle: List<UserSummaryDomainModel> = emptyList()
) {
    companion object {
        val Initial = UserProfileDomainModel()
    }
}

data class UserSummaryDomainModel(
    val uid: String = "",
    val name: String? = "",
    val pseudo: String = "",
    val avatarUrl: String? = "",
    val commonMoviesCount: Int? = 0
) {
    companion object {
        val Initial = UserSummaryDomainModel()
    }
}
