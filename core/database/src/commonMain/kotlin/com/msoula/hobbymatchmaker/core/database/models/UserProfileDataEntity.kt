package com.msoula.hobbymatchmaker.core.database.models

data class UserProfileDataEntity(
    val uid: String,
    val name: String,
    val pseudo: String,
    val avatarUrl: String?,
    val bio: String?,
    val interests: List<String>,
    val likedCount: Int,
    val circleCount: Int
)
