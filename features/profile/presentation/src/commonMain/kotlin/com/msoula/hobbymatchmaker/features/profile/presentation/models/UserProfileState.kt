package com.msoula.hobbymatchmaker.features.profile.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class UserProfileState(
    val profileState: UserProfileUiStateModel,
    val editableProfile: UserProfileUiModel?,
    val isEditMode: Boolean,
    val isPseudoAvailable: Boolean?
)
