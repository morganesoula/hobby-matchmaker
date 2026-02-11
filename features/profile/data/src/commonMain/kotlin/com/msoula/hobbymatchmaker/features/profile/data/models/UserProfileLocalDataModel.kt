package com.msoula.hobbymatchmaker.features.profile.data.models

data class UserProfileLocalDataModel(
    val uid: String = "",
    val name: String = "",
    val pseudo: String = "",
    val avatarUrl: String? = null,
    val bio: String? = null,
    val interests: List<String> = emptyList(),
    val likedCount: Int = 0,
    val circleCount: Int = 0
) {
    companion object {
        val Initial = UserProfileLocalDataModel()
    }
}
