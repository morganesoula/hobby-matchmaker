package com.msoula.hobbymatchmaker.presentation.di

import com.msoula.hobbymatchmaker.presentation.AppViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appViewModelModule = module {
    viewModel {
        AppViewModel(
            ioDispatcher = get(),
            logOutUseCase = get(),
            observeIsConnectedUseCase = get(),
            setIsConnectedUseCase = get()
        )
    }
}
