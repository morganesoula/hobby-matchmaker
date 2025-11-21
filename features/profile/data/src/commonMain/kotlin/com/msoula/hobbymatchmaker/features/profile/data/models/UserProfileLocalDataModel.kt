package com.msoula.hobbymatchmaker.features.profile.data.models

data class UserProfileLocalDataModel(
    val uid: String,
    val name: String,
    val pseudo: String,
    val avatarUrl: String?,
    val bio: String?,
    val interests: List<String>,
    val likedCount: Int,
    val circleCount: Int
)
