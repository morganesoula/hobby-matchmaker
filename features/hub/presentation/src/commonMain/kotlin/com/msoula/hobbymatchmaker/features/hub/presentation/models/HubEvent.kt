package com.msoula.hobbymatchmaker.features.hub.presentation.models

import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember

sealed interface HubEvent {
    data class OnRecentMatchClicked(val member: ProfileSocialMember) : HubEvent
    data object OnModalDismissed : HubEvent
}
