package com.msoula.hobbymatchmaker.features.social.presentation.models

sealed interface SocialUiEventModel {
    data class OnSearchPeople(val value: String) : SocialUiEventModel
    data class OnInviteToSocialCircle(val pseudo: String, val name: String?) : SocialUiEventModel
    data class OnAcceptInvitation(val inviteId: String) : SocialUiEventModel
    data class OnDeclineInvitation(val inviteId: String) : SocialUiEventModel
    data class OnCancelInvitation(val inviteId: String) : SocialUiEventModel
}
