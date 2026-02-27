package com.msoula.hobbymatchmaker.features.profile.presentation.mappers

import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.SocialMemberUiModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel

fun SocialMemberUiModel.toProfileSocialMembers(): ProfileSocialMember {
    return ProfileSocialMember(
        uid = uid,
        name = name,
        pseudo = pseudo,
        avatarUrl = avatarUrl,
        commonMoviesCount = commonMoviesCount
    )
}

fun SocialMemberUiModel.toUserSummaryDomainModel(): UserSummaryDomainModel =
    UserSummaryDomainModel(
        uid = uid,
        name = name ?: UserSummaryDomainModel.Initial.name,
        pseudo = pseudo,
        avatarUrl = avatarUrl,
        commonMoviesCount = commonMoviesCount ?: UserSummaryDomainModel.Initial.commonMoviesCount
    )

fun SocialMemberDomainModel.toSocialMemberUiModel(
    commonMoviesCount: Int
): SocialMemberUiModel =
    SocialMemberUiModel(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        commonMoviesCount = commonMoviesCount
    )
