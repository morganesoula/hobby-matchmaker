package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.core.user.domain.models.UserSummaryDomainModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialCircleMemberLocalDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialInvitationDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.InviteDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.InviteStatusData
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleMemberRemoteDataModel
import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun SocialInviteDomainModel.toInviteData(): InviteDataModel {
    return InviteDataModel(
        inviteId = inviteId,
        fromUid = fromUid,
        fromPseudo = fromPseudo ?: InviteDataModel.Initial.fromPseudo,
        toPseudo = toPseudo ?: InviteDataModel.Initial.toPseudo,
        name = name,
        status = status.toInviteStatusData(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun SocialMemberDomainModel.toSocialCircleMember(ownerUid: String): SocialCircleMemberRemoteDataModel {
    return SocialCircleMemberRemoteDataModel(
        uid = this.uid,
        ownerUid = ownerUid,
        pseudo = this.pseudo,
        name = this.name,
        avatarUrl = this.avatarUrl,
        moviesLiked = this.moviesLiked,
        commonMoviesCount = this.commonMoviesCount
    )
}

fun SocialCircleMemberRemoteDataModel.toSocialMemberDomainModel(): SocialMemberDomainModel =
    SocialMemberDomainModel(
        uid = this.uid,
        pseudo = this.pseudo,
        name = this.name,
        avatarUrl = this.avatarUrl,
        moviesLiked = this.moviesLiked,
        commonMoviesCount = this.commonMoviesCount
            ?: SocialMemberDomainModel.Initial.commonMoviesCount
    )

fun SocialCircleMemberRemoteDataModel.toSocialCircleMemberDataModel(): SocialCircleMemberLocalDataModel =
    SocialCircleMemberLocalDataModel(
        ownerUid = this.ownerUid,
        memberUid = this.uid,
        memberPseudo = this.pseudo,
        memberName = this.name,
        memberAvatarUrl = this.avatarUrl
    )

fun UserSummaryDomainModel.toSocialMemberDomainModel(commonMoviesCount: Int = 0) =
    SocialMemberDomainModel(
        uid = this.uid,
        pseudo = this.pseudo,
        name = this.name,
        avatarUrl = this.avatarUrl,
        moviesLiked = this.moviesLiked,
        commonMoviesCount = commonMoviesCount
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

fun InviteDataModel.toSocialInvitationDataModel(): SocialInvitationDataModel =
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
