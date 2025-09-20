package com.msoula.hobbymatchmaker.core.database.di

import app.cash.sqldelight.db.SqlDriver
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import org.koin.dsl.module

actual val coreModuleDaoPlatformSpecific = module {
    single<HMMDatabase> {
        HMMDatabase(get<SqlDriver>())
    }
}
