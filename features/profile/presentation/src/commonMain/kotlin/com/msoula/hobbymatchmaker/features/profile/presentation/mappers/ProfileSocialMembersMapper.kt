package com.msoula.hobbymatchmaker.features.profile.presentation.mappers

import com.msoula.hobbymatchmaker.core.design.organisms.ProfileSocialMembers
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.SocialMemberUiModel

fun SocialMemberUiModel.toProfileSocialMembers(): ProfileSocialMembers {
    return ProfileSocialMembers(
        uid = uid,
        name = name,
        pseudo = pseudo,
        avatarUrl = avatarUrl
    )
}

fun SocialMemberUiModel.toUserSummaryDomainModel(): UserSummaryDomainModel =
    UserSummaryDomainModel(
        uid = uid,
        name = name,
        pseudo = pseudo,
        avatarUrl = avatarUrl
    )
