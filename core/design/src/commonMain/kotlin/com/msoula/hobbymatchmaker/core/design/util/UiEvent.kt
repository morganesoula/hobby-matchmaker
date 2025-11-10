package com.msoula.hobbymatchmaker.core.design.util

sealed interface UiEvent {
    data class ShowSnackBar(val message: UIText) : UiEvent
    data class NavigateToRoute(val route: String) : UiEvent
    data class NavigateToDetail(val id: Long) : UiEvent
    data class OnDataReady(val data: String) : UiEvent
    data class OpenDialog(val title: UIText, val message: UIText) : UiEvent
}
