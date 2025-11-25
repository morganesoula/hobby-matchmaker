package com.msoula.hobbymatchmaker.core.network.di

import com.msoula.hobbymatchmaker.core.network.AndroidNetworkConnectivityChecker
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import org.koin.dsl.module

actual val coreModuleNetworkPlatformSpecific = module {
    single<NetworkConnectivityChecker> { AndroidNetworkConnectivityChecker(get()) }
}
