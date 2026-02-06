package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.Invite
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleMember
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Invite.toSocialInviteDomainModel(): SocialInviteDomainModel {
    return SocialInviteDomainModel(
        inviteId = inviteId,
        fromUid = fromUid,
        fromPseudo = fromPseudo,
        toPseudo = toPseudo,
        name = name,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

@OptIn(ExperimentalTime::class)
fun SocialInviteDomainModel.toInviteData(): Invite {
    return Invite(
        inviteId = inviteId,
        fromUid = fromUid,
        fromPseudo = fromPseudo ?: Invite.Initial.fromPseudo,
        toPseudo = toPseudo ?: Invite.Initial.toPseudo,
        name = name,
        status = status,
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
