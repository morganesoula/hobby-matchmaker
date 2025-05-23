package com.msoula.hobbymatchmaker.core.database.di

import com.msoula.hobbymatchmaker.core.database.services.MovieDAO
import com.msoula.hobbymatchmaker.core.database.services.MovieDAOImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleDAO = module {
    includes(coreModuleDaoPlatformSpecific)
    singleOf(::MovieDAOImpl) bind MovieDAO::class
}

expect val coreModuleDaoPlatformSpecific: Module
