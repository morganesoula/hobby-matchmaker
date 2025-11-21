package com.msoula.hobbymatchmaker.features.profile.presentation.models

sealed interface UserProfileUiEventModel {
    data class OnNameChanged(val value: String) : UserProfileUiEventModel
    data class OnBioChanged(val value: String) : UserProfileUiEventModel
    data class OnInterestsChanged(val value: List<String>) : UserProfileUiEventModel
    data class OnAvatarSelected(val path: String) : UserProfileUiEventModel
    data object OnSaveClicked : UserProfileUiEventModel
    data object OnEditModeClicked : UserProfileUiEventModel
    data object OnSignUpButtonClicked : UserProfileUiEventModel
}
