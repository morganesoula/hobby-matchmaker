package com.msoula.hobbymatchmaker.features.profile.presentation.models

import com.msoula.hobbymatchmaker.core.design.util.UIText

sealed interface UserProfileScreenStateModel {
    data object Loading : UserProfileScreenStateModel
    data class Success(val profile: UserProfileUiModel) : UserProfileScreenStateModel
    data class Guest(val uid: String) : UserProfileScreenStateModel
    data class Incomplete(val uid: String) : UserProfileScreenStateModel
    data class Error(val error: UIText) : UserProfileScreenStateModel
}
