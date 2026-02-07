package com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers

import com.msoula.hobbymatchmaker.core.database.models.UserProfileDataEntity
import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileLocalDataModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileNoCircleDomainModel

fun UserProfileDataEntity.toUserProfileLocalDataModel(): UserProfileLocalDataModel =
    UserProfileLocalDataModel(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        bio = this.bio,
        interests = this.interests,
        likedCount = this.likedCount,
        circleCount = this.circleCount
    )

fun UserProfileLocalDataModel.toUserProfileDataEntity(): UserProfileDataEntity =
    UserProfileDataEntity(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        bio = this.bio,
        interests = this.interests,
        likedCount = this.likedCount,
        circleCount = this.circleCount
    )

fun UserProfileLocalDataModel.toUserProfileNoCircleDomainModel(): UserProfileNoCircleDomainModel =
    UserProfileNoCircleDomainModel(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        bio = this.bio,
        interests = this.interests,
        likedMoviesCount = this.likedCount
    )

fun UserProfileDomainModel.toUserProfileLocalDataModel(): UserProfileLocalDataModel =
    UserProfileLocalDataModel(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        bio = this.bio,
        interests = this.interests,
        likedCount = this.likedMoviesCount,
        circleCount = this.socialCircle.size
    )
