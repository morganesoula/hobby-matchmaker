package com.msoula.hobbymatchmaker.core.database.di

import com.msoula.hobbymatchmaker.core.database.AndroidDriverFactory
import com.msoula.hobbymatchmaker.core.database.DriverFactory
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext

actual val coreModuleDatabasePlatformSpecific = module {
    single<DriverFactory> { AndroidDriverFactory(androidContext()) }
}
