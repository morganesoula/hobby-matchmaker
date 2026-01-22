package com.msoula.hobbymatchmaker.core.design.util

import androidx.compose.runtime.Immutable

@Immutable
sealed interface UiEvent {
    data class ShowSnackBar(val message: UIText) : UiEvent
    data class Navigate(val destination: NavigationDestination) : UiEvent
    data class OnDataReady(val data: String) : UiEvent
    data class OpenDialog(val dialogPurpose: String) : UiEvent
    data class CloseDialog(val dialogName: String) : UiEvent
    data object CapacityReached : UiEvent
}

sealed class NavigationDestination {
    data class MovieDetail(val movieId: Long) : NavigationDestination()
    data object Movies : NavigationDestination()
    data object Profile : NavigationDestination()
    data object SignIn : NavigationDestination()
    data object SignUp : NavigationDestination()
    data object Social : NavigationDestination()
}
