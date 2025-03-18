package com.msoula.hobbymatchmaker.presentation.di

import com.msoula.hobbymatchmaker.presentation.AppViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { AppViewModel(get(), get()) }
}
