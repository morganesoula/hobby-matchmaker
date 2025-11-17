package com.msoula.hobbymatchmaker.features.profile.presentation.models

import com.msoula.hobbymatchmaker.core.design.util.UIText

sealed interface UserProfileUiStateModel {
    object Loading : UserProfileUiStateModel
    data class Error(val errorMessage: UIText) : UserProfileUiStateModel
    data class Success(val userProfile: UserProfileUiModel) : UserProfileUiStateModel
    data object Guest : UserProfileUiStateModel
}
