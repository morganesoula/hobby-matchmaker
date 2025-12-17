package com.msoula.hobbymatchmaker.features.profile.presentation.models

import androidx.compose.runtime.Immutable
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUiEventModel

@Immutable
data class UserProfileActions(
    val closeEdition: () -> Unit,
    val logOut: (String) -> Unit,
    val onNavigate: (String) -> Unit,
    val onEvent: (UserProfileUiEventModel) -> Unit,
    val onSocialEvent: (SocialUiEventModel) -> Unit
)
