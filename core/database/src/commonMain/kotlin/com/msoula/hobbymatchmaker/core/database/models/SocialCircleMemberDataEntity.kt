package com.msoula.hobbymatchmaker.core.database.models

data class SocialCircleMemberDataEntity(
    val uid: String,
    val memberUid: String,
    val memberName: String?,
    val memberAvatarUrl: String?
)
