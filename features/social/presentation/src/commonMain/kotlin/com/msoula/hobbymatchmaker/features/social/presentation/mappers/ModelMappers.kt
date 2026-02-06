package com.msoula.hobbymatchmaker.features.social.presentation.mappers

import com.msoula.hobbymatchmaker.core.common.toTimeAgo
import com.msoula.hobbymatchmaker.core.design.models.Invitation
import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.InviteStatusUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.InviteUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUserSummaryUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.ExperimentalTime

fun SocialMemberDomainModel.toSocialSummaryUiModel(): SocialUserSummaryUiModel =
    SocialUserSummaryUiModel(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        commonMoviesCount = this.commonMoviesCount
    )

@OptIn(ExperimentalTime::class)
fun SocialInviteDomainModel.toIncomingInviteUiModel(): InviteUiModel =
    InviteUiModel(
        inviteId = this.inviteId,
        ownerId = "",
        ownerPseudo = this.toPseudo ?: InviteUiModel.Initial.ownerPseudo,
        guestUid = this.fromUid,
        guestName = this.name ?: "",
        guestPseudo = this.fromPseudo ?: InviteUiModel.Initial.guestPseudo,
        guestAvatarUrl = "",
        inviteTime = this.createdAt.epochSeconds,
        inviteStatus = this.status.toInviteStatusUiModel()
    )

@OptIn(ExperimentalTime::class)
fun SocialInviteDomainModel.toSentInviteUiModel(): InviteUiModel =
    InviteUiModel(
        inviteId = this.inviteId,
        ownerId = this.fromUid,
        ownerPseudo = this.fromPseudo ?: InviteUiModel.Initial.guestPseudo,
        guestUid = "",
        guestName = this.name ?: "",
        guestPseudo = this.toPseudo ?: InviteUiModel.Initial.ownerPseudo,
        guestAvatarUrl = "",
        inviteTime = this.createdAt.epochSeconds,
        inviteStatus = this.status.toInviteStatusUiModel()
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

fun ImmutableList<InviteUiModel>.toReceivedInvitations(): ImmutableList<Invitation> {
    return map { it.toInvitation() }.toImmutableList()
}

fun ImmutableList<InviteUiModel>.toSentInvitations(): ImmutableList<Invitation> {
    return map { it.toInvitation() }.toImmutableList()
}

fun InviteStatus.toInviteStatusUiModel(): InviteStatusUiModel =
    when (this) {
        InviteStatus.ACCEPTED -> InviteStatusUiModel.ACCEPTED
        InviteStatus.DECLINED -> InviteStatusUiModel.DECLINED
        InviteStatus.PENDING -> InviteStatusUiModel.PENDING
    }
