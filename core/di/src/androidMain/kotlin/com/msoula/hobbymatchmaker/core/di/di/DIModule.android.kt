package com.msoula.hobbymatchmaker.core.di.di

import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

actual val coreModuleDIPlatformSpecific = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
    single { StringResourcesProvider(get()) }
}
