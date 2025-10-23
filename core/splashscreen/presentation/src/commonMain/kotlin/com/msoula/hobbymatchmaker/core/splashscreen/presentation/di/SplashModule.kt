package com.msoula.hobbymatchmaker.core.splashscreen.presentation.di

import com.msoula.hobbymatchmaker.core.splashscreen.presentation.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val module = module {
    viewModel<SplashViewModel> { get() }
}
