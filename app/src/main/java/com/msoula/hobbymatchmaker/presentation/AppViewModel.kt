package com.msoula.hobbymatchmaker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.AuthUiStateModel
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.GetConnexionModeUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SetIsConnectedUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val logOutUseCase: LogOutUseCase,
    private val observeIsConnectedUseCase: ObserveIsConnectedUseCase,
    private val getConnexionModeUseCase: GetConnexionModeUseCase,
    private val setIsConnectedUseCase: SetIsConnectedUseCase
) : ViewModel() {

    val authenticationState: StateFlow<AuthUiStateModel> by lazy {
        observeIsConnectedUseCase()
            .mapLatest { isConnected ->
                if (isConnected) AuthUiStateModel.IsConnected else AuthUiStateModel.NotConnected
            }
            .flowOn(Dispatchers.Main)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                AuthUiStateModel.CheckingState
            )
    }

    fun logOut() {
        viewModelScope.launch(ioDispatcher) {
            val connexionMode = getConnexionModeUseCase().first()
            logOutUseCase(connexionMode ?: "EMAIL").mapSuccess {
                setIsConnectedUseCase(false)
            }
        }
    }
}
