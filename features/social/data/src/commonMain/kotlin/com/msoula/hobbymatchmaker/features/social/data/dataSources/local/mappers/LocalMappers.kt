package com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers

import com.msoula.hobbymatchmaker.core.database.models.SocialCircleMemberDataEntity
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel

fun SocialMemberDomainModel.toSocialCircleMemberDataEntity(
    ownerUid: String
): SocialCircleMemberDataEntity =
    SocialCircleMemberDataEntity(
        uid = ownerUid,
        memberUid = this.uid,
        memberPseudo = this.pseudo,
        memberName = this.name,
        memberAvatarUrl = this.avatarUrl
    )
