package com.msoula.hobbymatchmaker.features.social.presentation.mappers

import com.msoula.hobbymatchmaker.core.common.toTimeAgo
import com.msoula.hobbymatchmaker.core.design.models.CircleMember
import com.msoula.hobbymatchmaker.core.design.models.Invitation
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.social_report_reason_fake_profile
import com.msoula.hobbymatchmaker.core.design.social_report_reason_harassment
import com.msoula.hobbymatchmaker.core.design.social_report_reason_inappropriate_content
import com.msoula.hobbymatchmaker.core.design.social_report_reason_other
import com.msoula.hobbymatchmaker.core.design.social_report_reason_spam
import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import com.msoula.hobbymatchmaker.features.social.domain.models.ReportReason
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.CircleMemberUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.InviteStatusUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.InviteUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUserSummaryUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.StringResource
import kotlin.time.ExperimentalTime

fun SocialMemberDomainModel.toSocialSummaryUiModel(): SocialUserSummaryUiModel =
    SocialUserSummaryUiModel(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        commonMoviesCount = this.commonMoviesCount
    )

fun SocialMemberDomainModel.toCircleMemberUiModel(): CircleMemberUiModel =
    CircleMemberUiModel(
        uid = this.uid,
        name = this.name,
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl,
        commonMoviesCount = this.commonMoviesCount
    )

fun CircleMemberUiModel.toCircleMember(): CircleMember =
    CircleMember(
        uid = this.uid,
        name = this.name.orEmpty(),
        pseudo = this.pseudo,
        avatarUrl = this.avatarUrl.orEmpty(),
        commonMoviesCount = this.commonMoviesCount
    )

fun ImmutableList<CircleMemberUiModel>.toCircleMembers(): ImmutableList<CircleMember> =
    map { it.toCircleMember() }.toImmutableList()

@OptIn(ExperimentalTime::class)
fun SocialInviteDomainModel.toIncomingInviteUiModel(): InviteUiModel =
    InviteUiModel(
        inviteId = this.inviteId,
        ownerId = "",
        ownerPseudo = this.toPseudo ?: InviteUiModel.Initial.ownerPseudo,
        guestUid = this.fromUid,
        guestName = this.counterpartName.orEmpty(),
        guestPseudo = this.fromPseudo ?: InviteUiModel.Initial.guestPseudo,
        guestAvatarUrl = this.counterpartAvatarUrl.orEmpty(),
        commonMoviesCount = this.commonMoviesCount,
        inviteTime = this.createdAt.epochSeconds,
        inviteStatus = this.status.toInviteStatusUiModel()
    )

@OptIn(ExperimentalTime::class)
fun SocialInviteDomainModel.toSentInviteUiModel(): InviteUiModel =
    InviteUiModel(
        inviteId = this.inviteId,
        ownerId = this.fromUid,
        ownerPseudo = this.fromPseudo ?: InviteUiModel.Initial.guestPseudo,
        guestUid = this.counterpartUid.orEmpty(),
        guestName = this.counterpartName ?: this.name.orEmpty(),
        guestPseudo = this.toPseudo ?: InviteUiModel.Initial.ownerPseudo,
        guestAvatarUrl = this.counterpartAvatarUrl.orEmpty(),
        commonMoviesCount = this.commonMoviesCount,
        inviteTime = this.createdAt.epochSeconds,
        inviteStatus = this.status.toInviteStatusUiModel()
    )

@OptIn(ExperimentalTime::class)
fun InviteUiModel.toInvitation(isBusy: Boolean = false): Invitation =
    Invitation(
        ownerId = this.ownerId,
        invitationId = this.inviteId,
        invitationGuestUid = this.guestUid,
        invitationGuestName = this.guestName,
        invitationGuestPseudo = this.guestPseudo,
        invitationGuestAvatarUrl = this.guestAvatarUrl,
        invitationTime = this.inviteTime.toTimeAgo(),
        invitationStatus = this.inviteStatus.name,
        commonMoviesCount = this.commonMoviesCount,
        isBusy = isBusy
    )

fun ImmutableList<InviteUiModel>.toReceivedInvitations(busyInviteIds: Set<String>): ImmutableList<Invitation> {
    return map { it.toInvitation(isBusy = it.inviteId in busyInviteIds) }.toImmutableList()
}

fun ImmutableList<InviteUiModel>.toSentInvitations(busyInviteIds: Set<String>): ImmutableList<Invitation> {
    return map { it.toInvitation(isBusy = it.inviteId in busyInviteIds) }.toImmutableList()
}

fun InviteStatus.toInviteStatusUiModel(): InviteStatusUiModel =
    when (this) {
        InviteStatus.ACCEPTED -> InviteStatusUiModel.ACCEPTED
        InviteStatus.DECLINED -> InviteStatusUiModel.DECLINED
        InviteStatus.PENDING -> InviteStatusUiModel.PENDING
    }

fun ReportReason.toLabelResource(): StringResource = when (this) {
    ReportReason.SPAM -> Res.string.social_report_reason_spam
    ReportReason.HARASSMENT -> Res.string.social_report_reason_harassment
    ReportReason.FAKE_PROFILE -> Res.string.social_report_reason_fake_profile
    ReportReason.INAPPROPRIATE_CONTENT -> Res.string.social_report_reason_inappropriate_content
    ReportReason.OTHER -> Res.string.social_report_reason_other
}
