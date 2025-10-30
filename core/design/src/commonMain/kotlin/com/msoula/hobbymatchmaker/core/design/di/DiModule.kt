package com.msoula.hobbymatchmaker.core.design.di

import com.msoula.hobbymatchmaker.core.design.util.DefaultErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleDi = module {
    single { DefaultErrorMessageMapper } bind ErrorMessageMapper::class
}
