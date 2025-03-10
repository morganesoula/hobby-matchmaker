package com.msoula.hobbymatchmaker.presentation.di

import com.msoula.hobbymatchmaker.presentation.AppViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    factoryOf(::AppViewModel)
}
