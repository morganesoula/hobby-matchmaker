package com.msoula.hobbymatchmaker.features.profile.presentation.models

sealed interface UserProfileUiEventModel {
    data class OnNameChanged(val value: String) : UserProfileUiEventModel
    data class OnBioChanged(val value: String) : UserProfileUiEventModel

    data object OnPickAvatarClicked : UserProfileUiEventModel
    data object OnSaveClicked : UserProfileUiEventModel
    data object OnSkipClicked : UserProfileUiEventModel
}
