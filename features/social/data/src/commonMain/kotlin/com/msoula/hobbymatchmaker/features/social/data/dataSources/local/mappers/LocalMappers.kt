package com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers

import com.msoula.hobbymatchmaker.core.database.Social_invitation
import com.msoula.hobbymatchmaker.core.database.models.SocialCircleMemberDataEntity
import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import kotlin.time.Instant

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


fun Social_invitation.toSocialInviteDomainModel(): SocialInviteDomainModel =
    SocialInviteDomainModel(
        inviteId = this.id,
        name = this.name,
        fromUid = this.from_uid,
        fromPseudo = this.from_pseudo,
        toPseudo = this.to_pseudo,
        status = this.status.toInviteStatus(),
        createdAt = Instant.fromEpochMilliseconds(this.created_at),
        updatedAt = this.updated_at?.let { Instant.fromEpochMilliseconds(it) }
    )

fun SocialInviteDomainModel.toSocialInvitation(): Social_invitation =
    Social_invitation(
        id = this.inviteId,
        from_uid = this.fromUid,
        from_pseudo = this.fromPseudo,
        to_pseudo = this.toPseudo,
        name = this.name,
        status = this.status.name,
        created_at = this.createdAt.toEpochMilliseconds(),
        updated_at = this.updatedAt?.toEpochMilliseconds()
    )

fun String.toInviteStatus(): InviteStatus {
    return when (this) {
        "PENDING" -> InviteStatus.PENDING
        "ACCEPTED" -> InviteStatus.ACCEPTED
        else -> InviteStatus.DECLINED
    }
}
