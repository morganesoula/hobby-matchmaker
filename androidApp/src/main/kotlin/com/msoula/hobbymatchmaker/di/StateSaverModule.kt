package com.msoula.hobbymatchmaker.di

import androidx.lifecycle.SavedStateHandle
import org.koin.dsl.module

val coreCommonAndroidSavedStateHandle = module {
    single { SavedStateHandle() }
}
