package com.msoula.hobbymatchmaker.features.settings.presentation.models

sealed interface SettingsEventModel {
    data object OnLogOutClicked : SettingsEventModel
    data object OnDeleteAccountClicked : SettingsEventModel
    data object OnDeleteAccountConfirmed : SettingsEventModel
    data object OnDeleteAccountDismissed : SettingsEventModel
}
