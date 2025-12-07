package com.msoula.hobbymatchmaker.features.social.presentation.mappers

import com.msoula.hobbymatchmaker.core.design.models.Invitation
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.InviteUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUserSummaryUiModel

fun SocialMemberDomainModel.toSocialSummaryUiModel(): SocialUserSummaryUiModel =
    SocialUserSummaryUiModel(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl
    )

fun InviteUiModel.toInvitation(): Invitation =
    Invitation(
        ownerId = this.ownerId,
        invitationId = this.inviteId,
        invitationGuestName = this.guestName,
        invitationGuestPseudo = this.guestPseudo,
        invitationGuestAvatarUrl = this.guestAvatarUrl,
        invitationTime = this.inviteTime,
        invitationStatus = this.inviteStatus
    )

fun List<InviteUiModel>.toListInvitation(): List<Invitation> {
    return map { it.toInvitation() }
}
