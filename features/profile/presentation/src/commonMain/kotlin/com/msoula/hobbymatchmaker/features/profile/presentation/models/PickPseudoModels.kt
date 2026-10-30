package com.msoula.hobbymatchmaker.features.profile.presentation.models

import androidx.compose.runtime.Immutable
import com.msoula.hobbymatchmaker.features.profile.domain.models.PseudoValidationError

sealed interface PickPseudoEventModel {
    data class OnFirstNameChanged(val value: String) : PickPseudoEventModel
    data class OnPseudoChanged(val value: String) : PickPseudoEventModel
    data object OnContinueClicked : PickPseudoEventModel
    data object OnLogOutClicked : PickPseudoEventModel
}

@Immutable
sealed interface PickPseudoStatusModel {
    data object Idle : PickPseudoStatusModel
    data class Invalid(val errors: Set<PseudoValidationError>) : PickPseudoStatusModel
    data object Checking : PickPseudoStatusModel
    data object Available : PickPseudoStatusModel
    data object Taken : PickPseudoStatusModel
}

@Immutable
data class PickPseudoUiModel(
    val firstName: String = "",
    val isFirstNameEditable: Boolean = true,
    val pseudo: String = "",
    val status: PickPseudoStatusModel = PickPseudoStatusModel.Idle,
    val isSubmitting: Boolean = false
) {
    val canSubmit: Boolean
        get() = status == PickPseudoStatusModel.Available && firstName.isNotBlank() && !isSubmitting

    companion object {
        val Initial = PickPseudoUiModel()
    }
}
