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
        avatarUrl = this.avatarUrl
    )

@OptIn(ExperimentalTime::class)
fun SocialInviteDomainModel.toInviteUiModel(): InviteUiModel =
    InviteUiModel(
        ownerId = this.fromUid,
        inviteId = this.inviteId,
        guestName = this.name ?: "",
        guestPseudo = this.toPseudo,
        guestAvatarUrl = "",
        inviteTime = this.createdAt.epochSeconds,
        inviteStatus = this.status
    )

@OptIn(ExperimentalTime::class)
fun InviteUiModel.toInvitation(): Invitation =
    Invitation(
        ownerId = this.ownerId,
        invitationId = this.inviteId,
        invitationGuestName = this.guestName,
        invitationGuestPseudo = this.guestPseudo,
        invitationGuestAvatarUrl = this.guestAvatarUrl,
        invitationTime = this.inviteTime.toTimeAgo(),
        invitationStatus = this.inviteStatus.name
    )

fun List<InviteUiModel>.toListInvitation(): List<Invitation> {
    return map { it.toInvitation() }
}
