package com.msoula.hobbymatchmaker.features.profile.presentation.models

import com.msoula.hobbymatchmaker.core.common.UIText

sealed interface UserProfileUiStateModel {
    object Loading : UserProfileUiStateModel
    data class Error(val errorMessage: UIText) : UserProfileUiStateModel
    data class Success(val userProfile: UserProfileUiModel) : UserProfileUiStateModel
    data class Incomplete(val uid: String) : UserProfileUiStateModel
    data class Guest(val uid: String) : UserProfileUiStateModel
}
