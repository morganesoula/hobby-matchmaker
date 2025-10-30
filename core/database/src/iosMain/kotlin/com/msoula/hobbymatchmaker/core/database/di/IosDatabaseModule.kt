package com.msoula.hobbymatchmaker.core.database.di

import com.msoula.hobbymatchmaker.core.database.DriverFactory
import com.msoula.hobbymatchmaker.core.database.IosDriverFactory
import org.koin.dsl.module

actual val coreModuleDatabasePlatformSpecific = module {
    single<DriverFactory> { IosDriverFactory() }
}
