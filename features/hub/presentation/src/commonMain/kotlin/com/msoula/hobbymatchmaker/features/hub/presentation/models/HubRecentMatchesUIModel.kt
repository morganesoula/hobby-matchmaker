package com.msoula.hobbymatchmaker.features.hub.presentation.models

data class HubRecentMatchesUIModel(
    val name: String = "",
    val pseudo: String = ""
) {
    companion object {
        val Initial = HubRecentMatchesUIModel()
    }
}
