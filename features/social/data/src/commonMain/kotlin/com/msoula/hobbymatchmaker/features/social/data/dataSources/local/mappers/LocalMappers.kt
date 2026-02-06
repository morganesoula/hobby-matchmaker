package com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers

import com.msoula.hobbymatchmaker.core.database.Social_invitation
import com.msoula.hobbymatchmaker.core.database.models.SocialCircleMemberDataEntity
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialCircleMemberDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialInvitationDataModel
import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import kotlin.time.Instant

fun String.toInviteStatus(): InviteStatus {
    return when (this) {
        "PENDING" -> InviteStatus.PENDING
        "ACCEPTED" -> InviteStatus.ACCEPTED
        else -> InviteStatus.DECLINED
    }
}

fun Social_invitation.toSocialInvitationDataModel(): SocialInvitationDataModel =
    SocialInvitationDataModel(
        id = this.id,
        fromUid = this.from_uid,
        fromPseudo = this.from_pseudo,
        toPseudo = this.to_pseudo,
        name = this.name,
        status = this.status,
        createdAt = this.created_at,
        updatedAt = this.updated_at
    )

fun SocialInvitationDataModel.toSocialInviteDomainModel(): SocialInviteDomainModel =
    SocialInviteDomainModel(
        inviteId = this.id,
        fromUid = this.fromUid,
        fromPseudo = this.fromPseudo,
        toPseudo = this.toPseudo,
        name = this.name,
        status = this.status.toInviteStatus(),
        createdAt = Instant.fromEpochMilliseconds(this.createdAt),
        updatedAt = this.updatedAt?.let { Instant.fromEpochMilliseconds(it) }
    )

fun SocialInvitationDataModel.toSocialInvitation(): Social_invitation =
    Social_invitation(
        id = this.id,
        from_uid = this.fromUid,
        from_pseudo = this.fromPseudo,
        to_pseudo = this.toPseudo,
        name = this.name,
        status = this.status,
        created_at = this.createdAt,
        updated_at = this.updatedAt
    )

fun SocialCircleMemberDataEntity.toSocialCircleMemberDataModel(): SocialCircleMemberDataModel =
    SocialCircleMemberDataModel(
        ownerUid = this.uid,
        memberUid = this.memberUid,
        memberPseudo = this.memberPseudo,
        memberName = this.memberName,
        memberAvatarUrl = this.memberAvatarUrl
    )

fun SocialCircleMemberDataModel.toSocialCircleMemberDataEntity(): SocialCircleMemberDataEntity =
    SocialCircleMemberDataEntity(
        uid = this.ownerUid,
        memberUid = this.memberUid,
        memberPseudo = this.memberPseudo,
        memberName = this.memberName,
        memberAvatarUrl = this.memberAvatarUrl,
    )
