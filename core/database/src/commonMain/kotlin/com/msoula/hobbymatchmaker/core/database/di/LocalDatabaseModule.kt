package com.msoula.hobbymatchmaker.core.database.di

import com.msoula.hobbymatchmaker.core.database.HMMLocalDatabase
import com.msoula.hobbymatchmaker.core.database.dao.MovieDAO
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModuleDAO = module {
    includes(coreModuleDaoPlatformSpecific)

    single<MovieDAO> {
        get<HMMLocalDatabase>().movieDao()
    }
}

expect val coreModuleDaoPlatformSpecific: Module
