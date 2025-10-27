package com.msoula.hobbymatchmaker.features.profile.presentation.mappers

import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.SocialMemberUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel

fun UserProfileDomainModel.toUserProfileUiModel(): UserProfileUiModel =
    UserProfileUiModel(
        name = this.name,
        avatarUrl = this.avatarUrl,
        interests = this.interests,
        moviesLikedCount = this.likedMoviesCount,
        socialMembersCount = this.socialCircle.size,
        socialMembers = this.socialCircle.map { model -> model.toSocialMemberUiModel() }
    )

fun UserSummaryDomainModel.toSocialMemberUiModel(): SocialMemberUiModel =
    SocialMemberUiModel(
        uid = this.uid,
        name = this.name,
        avatarUrl = this.avatarUrl
    )
