package com.msoula.hobbymatchmaker.features.profile.presentation.models

import androidx.compose.runtime.Immutable
import com.msoula.hobbymatchmaker.core.design.util.UIText

@Immutable
sealed interface UserProfileUiStateModel {
    object Loading : UserProfileUiStateModel
    data class Error(val errorMessage: UIText) : UserProfileUiStateModel
    data class Success(val userProfile: UserProfileUiModel) : UserProfileUiStateModel
    data object Guest : UserProfileUiStateModel
}
