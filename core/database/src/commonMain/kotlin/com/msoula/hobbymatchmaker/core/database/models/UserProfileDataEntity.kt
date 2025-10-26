package com.msoula.hobbymatchmaker.core.database.models

data class UserProfileDataEntity(
    val uid: Long,
    val name: String,
    val avatarUrl: String?,
    val bio: String?,
    val interests: List<String>?,
    val likedCount: Int,
    val circleCount: Int
)
