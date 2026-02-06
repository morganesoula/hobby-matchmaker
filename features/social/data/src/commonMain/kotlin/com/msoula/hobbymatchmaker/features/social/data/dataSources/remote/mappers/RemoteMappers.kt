package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialCircleMemberDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialInvitationDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.Invite
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.InviteStatusData
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleMember
import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun SocialInviteDomainModel.toInviteData(): Invite {
    return Invite(
        inviteId = inviteId,
        fromUid = fromUid,
        fromPseudo = fromPseudo ?: Invite.Initial.fromPseudo,
        toPseudo = toPseudo ?: Invite.Initial.toPseudo,
        name = name,
        status = status.toInviteStatusData(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun SocialMemberDomainModel.toSocialCircleMember(ownerUid: String): SocialCircleMember {
    return SocialCircleMember(
        uid = this.uid,
        ownerUid = ownerUid,
        pseudo = this.pseudo,
        name = this.name,
        avatarUrl = this.avatarUrl,
        moviesLiked = this.moviesLiked,
        commonMoviesCount = this.commonMoviesCount
    )
}

fun SocialCircleMember.toSocialMemberDomainModel(): SocialMemberDomainModel =
    SocialMemberDomainModel(
        uid = this.uid,
        pseudo = this.pseudo,
        name = this.name,
        avatarUrl = this.avatarUrl,
        moviesLiked = this.moviesLiked,
        commonMoviesCount = this.commonMoviesCount
    )

fun SocialCircleMember.toSocialCircleMemberDataModel(): SocialCircleMemberDataModel =
    SocialCircleMemberDataModel(
        ownerUid = this.ownerUid,
        memberUid = this.uid,
        memberPseudo = this.pseudo,
        memberName = this.name,
        memberAvatarUrl = this.avatarUrl
    )

fun InviteStatus.toInviteStatusData(): InviteStatusData =
    when (this) {
        InviteStatus.PENDING -> InviteStatusData.PENDING
        InviteStatus.ACCEPTED -> InviteStatusData.ACCEPTED
        InviteStatus.DECLINED -> InviteStatusData.DECLINED
    }

fun String.toInviteStatusData(): InviteStatusData =
    when (this) {
        "PENDING" -> InviteStatusData.PENDING
        "ACCEPTED" -> InviteStatusData.ACCEPTED
        else -> InviteStatusData.DECLINED
    }

fun Invite.toSocialInvitationDataModel(): SocialInvitationDataModel =
    SocialInvitationDataModel(
        id = this.inviteId,
        fromUid = this.fromUid,
        fromPseudo = this.fromPseudo,
        toPseudo = this.toPseudo,
        name = this.name,
        status = this.status.name,
        createdAt = this.createdAt.toEpochMilliseconds(),
        updatedAt = this.updatedAt?.toEpochMilliseconds()
    )
