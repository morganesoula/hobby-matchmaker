package com.msoula.hobbymatchmaker.core.common.di

import com.msoula.hobbymatchmaker.core.common.ImageFileManager
import com.msoula.hobbymatchmaker.core.common.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single { createDataStore(androidContext()) }
    single { ImageFileManager(get(), get()) }
}
