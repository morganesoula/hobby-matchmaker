package com.msoula.hobbymatchmaker.features.profile.presentation.models

import androidx.compose.runtime.Immutable
import com.msoula.hobbymatchmaker.core.design.util.NavigationDestination

@Immutable
data class UserProfileActions(
    val closeEdition: () -> Unit,
    val logOut: (String) -> Unit,
    val onNavigate: (NavigationDestination) -> Unit,
    val onEvent: (UserProfileUiEventModel) -> Unit,
    val onSearchPeople: (String) -> Unit,
    val onInviteToSocialCircle: (pseudo: String, name: String?) -> Unit
)