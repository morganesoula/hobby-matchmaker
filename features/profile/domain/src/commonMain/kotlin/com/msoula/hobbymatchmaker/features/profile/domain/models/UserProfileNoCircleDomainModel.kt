package com.msoula.hobbymatchmaker.features.profile.domain.models

data class UserProfileNoCircleDomainModel(
    val uid: String = "",
    val name: String = "",
    val pseudo: String = "",
    val avatarUrl: String? = "",
    val bio: String? = "",
    val interests: List<String> = emptyList(),
    val likedMoviesCount: Int = 0,
) {
    companion object {
        val Initial = UserProfileNoCircleDomainModel()
    }
}
