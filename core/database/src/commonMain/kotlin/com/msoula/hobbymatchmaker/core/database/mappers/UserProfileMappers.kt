package com.msoula.hobbymatchmaker.core.database.mappers

import com.msoula.hobbymatchmaker.core.database.User_profile
import com.msoula.hobbymatchmaker.core.database.models.UserProfileDataEntity

fun User_profile.toUserProfileDataEntity(): UserProfileDataEntity =
    UserProfileDataEntity(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatar_url,
        bio = this.bio,
        interests = this.interests_json,
        likedCount = this.liked_count.toInt(),
        circleCount = this.circle_count.toInt()
    )
