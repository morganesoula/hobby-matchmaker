package com.msoula.hobbymatchmaker.core.navigation.presentation

import com.arkivanov.decompose.ComponentContext
import com.msoula.hobbymatchmaker.core.navigation.domain.SplashComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.util.coroutineScope
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SplashComponentImpl(
    componentContext: ComponentContext,
    private val observeIsConnectedUseCase: ObserveIsConnectedUseCase
) : SplashComponent, ComponentContext by componentContext {

    private val _isConnected = MutableStateFlow<Boolean?>(null)
    override val isConnected: StateFlow<Boolean?>
        get() = _isConnected.asStateFlow()

    init {
        observeIsConnectedUseCase()
            .onEach { _isConnected.value = it }
            .launchIn(componentContext.coroutineScope())
    }
}
