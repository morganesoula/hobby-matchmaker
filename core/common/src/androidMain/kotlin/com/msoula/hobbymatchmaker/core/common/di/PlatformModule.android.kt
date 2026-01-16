package com.msoula.hobbymatchmaker.core.common.di

import com.msoula.hobbymatchmaker.core.common.ImageFileManager
import org.koin.dsl.module

actual val platformModule = module {
    single { ImageFileManager(get()) }
}