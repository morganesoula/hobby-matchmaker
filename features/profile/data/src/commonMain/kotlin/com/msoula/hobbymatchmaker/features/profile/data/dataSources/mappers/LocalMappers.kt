package com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers

import com.msoula.hobbymatchmaker.core.database.models.UserProfileDataEntity
import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileLocalDataModel

fun UserProfileDataEntity.toUserProfileLocalDataModel(): UserProfileLocalDataModel {
    return UserProfileLocalDataModel(
        uid = this.uid,
        name = this.name,
        avatarUrl = this.avatarUrl,
        bio = this.bio,
        interests = this.interests,
        likedCount = this.likedCount,
        circleCount = this.circleCount
    )
}

fun UserProfileLocalDataModel.toUserProfileDataEntity(): UserProfileDataEntity {
    return UserProfileDataEntity(
        uid = this.uid,
        name = this.name,
        avatarUrl = this.avatarUrl,
        bio = this.bio,
        interests = this.interests,
        likedCount = this.likedCount,
        circleCount = this.circleCount
    )
}
