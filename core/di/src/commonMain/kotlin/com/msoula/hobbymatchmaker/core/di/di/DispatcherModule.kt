package com.msoula.hobbymatchmaker.core.di.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val dispatcherModule = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
}
