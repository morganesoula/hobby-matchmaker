package com.msoula.hobbymatchmaker.core.database.di

import com.msoula.hobbymatchmaker.core.database.DatabaseDriver
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import org.koin.dsl.module

actual val coreModuleDaoPlatformSpecific = module {
    single<HMMDatabase> {
        HMMDatabase(DatabaseDriver().createDriver())
    }
}
