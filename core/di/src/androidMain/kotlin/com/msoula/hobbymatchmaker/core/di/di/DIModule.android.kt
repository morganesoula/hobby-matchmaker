package com.msoula.hobbymatchmaker.core.di.di

import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider
import org.koin.dsl.module

actual val coreDIModulePlatformSpecific = module {
    single { StringResourcesProvider(get()) }
}
