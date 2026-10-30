package com.msoula.hobbymatchmaker.core.design.models

import androidx.compose.runtime.Immutable
import com.msoula.hobbymatchmaker.core.design.molecules.ValidationRequirement
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface PseudoStatusUiModel {
    data object Idle : PseudoStatusUiModel
    data class Invalid(val requirements: ImmutableList<ValidationRequirement>) : PseudoStatusUiModel
    data object Checking : PseudoStatusUiModel
    data object Available : PseudoStatusUiModel
    data object Taken : PseudoStatusUiModel
}
