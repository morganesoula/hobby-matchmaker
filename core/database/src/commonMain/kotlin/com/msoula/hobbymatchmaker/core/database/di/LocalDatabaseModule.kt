package com.msoula.hobbymatchmaker.core.database.di

import com.msoula.hobbymatchmaker.core.database.HMMLocalDatabase
import com.msoula.hobbymatchmaker.core.database.dao.MovieDAO
import org.koin.dsl.module

val daoModule = module {
    single<MovieDAO> {
        get<HMMLocalDatabase>().movieDao()
    }
}
