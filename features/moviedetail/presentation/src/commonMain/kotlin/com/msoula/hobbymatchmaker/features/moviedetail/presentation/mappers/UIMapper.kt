package com.msoula.hobbymatchmaker.features.moviedetail.presentation.mappers

import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MatchingMemberUiModel

fun MatchingMemberUiModel.toProfileSocialMember(): ProfileSocialMember =
    ProfileSocialMember(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        commonMoviesCount = 0
    )
