package com.msoula.hobbymatchmaker.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.NavigationDestination
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.features.settings.presentation.models.AppVersionUiModel
import com.msoula.hobbymatchmaker.features.settings.presentation.models.SettingsEventModel
import com.msoula.hobbymatchmaker.features.settings.presentation.orchestrators.SettingsOrchestrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsInteractor: SettingsOrchestrator,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope

    private val eventHandler = EventHandler()
    val events = eventHandler.events

    val appVersion: StateFlow<AppVersionUiModel>
        field = MutableStateFlow(settingsInteractor.appVersion())

    val isDeleteAccountDialogVisible: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val isDeletingAccount: StateFlow<Boolean>
        field = MutableStateFlow(false)

    fun onEvent(event: SettingsEventModel) {
        when (event) {
            SettingsEventModel.OnLogOutClicked -> logOut()
            SettingsEventModel.OnDeleteAccountClicked -> isDeleteAccountDialogVisible.update { true }
            SettingsEventModel.OnDeleteAccountDismissed -> {
                if (!isDeletingAccount.value) isDeleteAccountDialogVisible.update { false }
            }

            SettingsEventModel.OnDeleteAccountConfirmed -> deleteAccount()
        }
    }

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }

    private fun logOut() {
        scope.launch {
            settingsInteractor.logOut()
                .onSuccess { eventHandler.sendEvent(UiEvent.Navigate(NavigationDestination.SignIn)) }
                .onFailure { error ->
                    eventHandler.sendEvent(UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error)))
                }
        }
    }

    private fun deleteAccount() {
        if (isDeletingAccount.value) return

        scope.launch {
            isDeletingAccount.update { true }
            settingsInteractor.deleteAccount()
                .onSuccess {
                    isDeleteAccountDialogVisible.update { false }
                    eventHandler.sendEvent(UiEvent.Navigate(NavigationDestination.SignIn))
                }
                .onFailure { error ->
                    Logger.e("Failed to delete account: $error")
                    isDeleteAccountDialogVisible.update { false }
                    eventHandler.sendEvent(UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error)))
                }

            isDeletingAccount.update { false }
        }
    }
}
