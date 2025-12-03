package com.msoula.hobbymatchmaker.features.social.presentation.models

sealed interface SocialUiEventModel {
    data class OnSearchPeople(val value: String) : SocialUiEventModel
    data class OnInviteToSocialCircle(val value: String) : SocialUiEventModel
}
