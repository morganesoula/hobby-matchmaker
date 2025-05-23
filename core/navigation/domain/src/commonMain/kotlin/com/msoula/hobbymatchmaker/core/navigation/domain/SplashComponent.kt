package com.msoula.hobbymatchmaker.core.navigation.domain

import kotlinx.coroutines.flow.StateFlow

interface SplashComponent {
    val isConnected: StateFlow<Boolean?>
}
