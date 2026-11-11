package com.msoula.hobbymatchmaker.features.social.presentation.models

import com.msoula.hobbymatchmaker.features.social.domain.models.ReportReason

sealed interface SocialUiEventModel {
    data class OnSearchPeople(val value: String) : SocialUiEventModel
    data class OnInviteToSocialCircle(val pseudo: String, val name: String?) : SocialUiEventModel
    data class OnAcceptInvitation(val inviteId: String, val guestUid: String) : SocialUiEventModel
    data class OnDeclineInvitation(val inviteId: String) : SocialUiEventModel
    data class OnCancelInvitation(val inviteId: String) : SocialUiEventModel
    data class OnDismissDeclinedInvitation(val inviteId: String) : SocialUiEventModel
    data class OnRemoveMember(val memberUid: String) : SocialUiEventModel
    data class OnBlockUser(val userUid: String) : SocialUiEventModel
    data class OnReportUser(val userUid: String, val reason: ReportReason) : SocialUiEventModel
    data object OnRetrySocialCircle : SocialUiEventModel
}
