package com.msoula.hobbymatchmaker.core.common.di

import androidx.lifecycle.SavedStateHandle
import org.koin.dsl.module

val coreCommonAndroidSavedStateHandle = module {
    single { SavedStateHandle() }
}
