package com.msoula.hobbymatchmaker.core.di.module

import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun provideStringResourcesPlatformModule(): Module = module {
    single { StringResourcesProvider(get()) }
}
