package com.msoula.hobbymatchmaker.features.profile.presentation.mappers

import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileNoCircleDomainModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel

fun UserProfileUiModel.toUserProfileDomainModel(uid: String): UserProfileDomainModel =
    UserProfileDomainModel(
        uid = uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        bio = this.bio,
        interests = this.interests ?: emptyList(),
        likedMoviesCount = this.moviesLikedCount
    )

fun UserProfileNoCircleDomainModel.toUserProfileUiModel(
    likedMoviesCount: Int
): UserProfileUiModel =
    UserProfileUiModel(
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        bio = this.bio,
        interests = this.interests,
        moviesLikedCount = likedMoviesCount
    )
