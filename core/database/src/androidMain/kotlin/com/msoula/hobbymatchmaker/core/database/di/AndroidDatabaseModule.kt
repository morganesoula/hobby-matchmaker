package com.msoula.hobbymatchmaker.core.database.di

import com.msoula.hobbymatchmaker.core.database.AndroidDriverFactory
import com.msoula.hobbymatchmaker.core.database.DriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val coreModuleDatabasePlatformSpecific = module {
    single<DriverFactory> { AndroidDriverFactory(androidContext()) }
}
