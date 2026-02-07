package com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers

import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileRemoteDataModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel

fun UserProfileDomainModel.toUserProfileRemoteDataModel(): UserProfileRemoteDataModel {
    return UserProfileRemoteDataModel(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        bio = this.bio,
        interests = this.interests
    )
}
