package com.msoula.hobbymatchmaker.features.profile.presentation.mappers

import com.msoula.hobbymatchmaker.core.design.organisms.ProfileSocialMembers
import com.msoula.hobbymatchmaker.features.profile.presentation.models.SocialMemberUiModel

fun SocialMemberUiModel.toProfileSocialMembers(): ProfileSocialMembers {
    return ProfileSocialMembers(
        uid = uid,
        name = name,
        avatarUrl = avatarUrl,
    )
}
