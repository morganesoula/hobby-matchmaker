package com.msoula.hobbymatchmaker.features.social.presentation.mappers

import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUserSummaryUiModel

fun SocialMemberDomainModel.toSocialSummaryUiModel(): SocialUserSummaryUiModel =
    SocialUserSummaryUiModel(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl
    )
