package com.msoula.hobbymatchmaker.core.database.di

import com.msoula.hobbymatchmaker.core.database.HMMLocalDatabase
import com.msoula.hobbymatchmaker.core.database.getDatabaseBuilder
import com.msoula.hobbymatchmaker.core.database.getHMMLocalDatabase
import org.koin.dsl.module

actual val coreModuleDaoPlatformSpecific = module {
    single<HMMLocalDatabase> {
        val builder = getDatabaseBuilder(context = get())
        getHMMLocalDatabase(builder)
    }
}
