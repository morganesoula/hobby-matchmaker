package com.msoula.hobbymatchmaker.features.movies.presentation.models

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.PersistentList

@Stable
sealed interface MovieUiStateModel {
    data object Loading : MovieUiStateModel

    data object Empty : MovieUiStateModel

    @Immutable
    data class Fetched(val list: PersistentList<MovieUiModel>) : MovieUiStateModel

    @Immutable
    data class Error(val errorMessage: String) : MovieUiStateModel
}
