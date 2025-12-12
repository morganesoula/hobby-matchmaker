package com.msoula.hobbymatchmaker.features.social.presentation.mappers

import com.msoula.hobbymatchmaker.core.common.toTimeAgo
import com.msoula.hobbymatchmaker.core.design.models.Invitation
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.InviteUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUserSummaryUiModel
import kotlin.time.ExperimentalTime

fun SocialMemberDomainModel.toSocialSummaryUiModel(): SocialUserSummaryUiModel =
    SocialUserSummaryUiModel(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        commonMoviesCount = this.commonMoviesCount
    )

fun SocialUserSummaryUiModel.toSocialMemberDomainModel(): SocialMemberDomainModel =
    SocialMemberDomainModel(
        uid = this.uid,
        pseudo = this.pseudo,
        name = this.name,
        avatarUrl = this.avatarUrl,
        commonMoviesCount = this.commonMoviesCount
    )

@OptIn(ExperimentalTime::class)
fun SocialInviteDomainModel.toInviteUiModel(): InviteUiModel =
    InviteUiModel(
        ownerId = this.fromUid,
        ownerPseudo = this.toPseudo,
        inviteId = this.inviteId,
        guestUid = this.fromUid,
        guestName = this.name ?: "",
        guestPseudo = this.fromPseudo,
        guestAvatarUrl = "",
        inviteTime = this.createdAt.epochSeconds,
        inviteStatus = this.status
    )

@OptIn(ExperimentalTime::class)
fun InviteUiModel.toInvitation(): Invitation =
    Invitation(
        ownerId = this.ownerId,
        invitationId = this.inviteId,
        invitationGuestUid = this.guestUid,
        invitationGuestName = this.guestName,
        invitationGuestPseudo = this.guestPseudo,
        invitationGuestAvatarUrl = this.guestAvatarUrl,
        invitationTime = this.inviteTime.toTimeAgo(),
        invitationStatus = this.inviteStatus.name
    )

fun List<InviteUiModel>.toReceivedInvitations(): List<Invitation> {
    return map { it.toInvitation() }
}

fun List<InviteUiModel>.toSentInvitations(): List<Invitation> {
    return map { invite ->
        Invitation(
            ownerId = invite.ownerId,
            invitationId = invite.inviteId,
            invitationGuestUid = invite.guestUid,
            invitationGuestName = invite.guestName,
            invitationGuestPseudo = invite.ownerPseudo,
            invitationGuestAvatarUrl = invite.guestAvatarUrl,
            invitationTime = invite.inviteTime.toTimeAgo(),
            invitationStatus = invite.inviteStatus.name
        )
    }
}
