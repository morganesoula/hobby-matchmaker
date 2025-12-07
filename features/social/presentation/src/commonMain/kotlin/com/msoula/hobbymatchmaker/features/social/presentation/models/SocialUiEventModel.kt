package com.msoula.hobbymatchmaker.features.social.presentation.models

sealed interface SocialUiEventModel {
    data class OnSearchPeople(val value: String) : SocialUiEventModel
    data class OnInviteToSocialCircle(val value: String) : SocialUiEventModel
    data class OnAcceptInvitation(val value: Long) : SocialUiEventModel
    data class OnDeclineInvitation(val value: Long) : SocialUiEventModel
    data class OnCancelInvitation(val value: Long) : SocialUiEventModel
}
