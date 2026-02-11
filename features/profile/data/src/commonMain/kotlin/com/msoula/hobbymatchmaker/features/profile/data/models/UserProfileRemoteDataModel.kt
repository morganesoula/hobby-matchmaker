package com.msoula.hobbymatchmaker.features.profile.data.models

data class UserProfileRemoteDataModel(
    val uid: String = "",
    val name: String? = null,
    val pseudo: String? = null,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val interests: List<String>? = null
) {
    companion object {
        val Initial = UserProfileRemoteDataModel()
    }
}
