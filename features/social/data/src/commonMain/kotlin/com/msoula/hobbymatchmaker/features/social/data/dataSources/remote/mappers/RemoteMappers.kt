package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.Invite
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Invite.toSocialInviteDomainModel(): SocialInviteDomainModel {
    return SocialInviteDomainModel(
        inviteId = inviteId,
        fromUid = fromUid,
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
        toPseudo = toPseudo,
        name = name,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
