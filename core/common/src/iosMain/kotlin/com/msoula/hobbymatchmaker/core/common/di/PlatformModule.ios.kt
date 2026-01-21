package com.msoula.hobbymatchmaker.core.common.di

import com.msoula.hobbymatchmaker.core.common.ImageFileManager
import com.msoula.hobbymatchmaker.core.common.createDataStore
import org.koin.dsl.module

actual val platformModule = module {
    single { createDataStore() }
    single { ImageFileManager() }
}
