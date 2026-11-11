package com.msoula.hobbymatchmaker.features.settings.presentation.models

data class AppVersionUiModel(
    val versionName: String = "",
    val buildNumber: String = ""
) {
    companion object {
        val Initial = AppVersionUiModel()
    }
}
