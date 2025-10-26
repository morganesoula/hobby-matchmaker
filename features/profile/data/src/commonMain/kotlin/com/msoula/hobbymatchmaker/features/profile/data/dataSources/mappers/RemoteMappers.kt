package com.msoula.hobbymatchmaker.features.profile.data.dataSources.mappers

import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileRemoteDataModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel

fun UserProfileDomainModel.toUserProfileRemoteDataModel(): UserProfileRemoteDataModel {
    return UserProfileRemoteDataModel(
        uid = this.uid.toString()
    )
}

fun UserProfileRemoteDataModel.toUserProfileDomainModel(): UserProfileDomainModel {
    return UserProfileDomainModel(
        uid = this.uid.toLong(),
        name = "",
        avatarUrl = "",
        bio = "",
        interests = emptyList(),
        likedMoviesCount = 0,
        socialCircle = emptyList(),
    )
}
