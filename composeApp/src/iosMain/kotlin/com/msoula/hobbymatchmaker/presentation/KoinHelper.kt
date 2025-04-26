package com.msoula.hobbymatchmaker.presentation

import com.msoula.hobbymatchmaker.presentation.navigation.appModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule())
    }
}
