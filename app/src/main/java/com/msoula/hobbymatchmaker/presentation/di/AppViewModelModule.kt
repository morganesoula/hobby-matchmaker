package com.msoula.hobbymatchmaker.presentation.di

import com.msoula.hobbymatchmaker.MainActivity
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.GoogleClient
import com.msoula.hobbymatchmaker.presentation.AppViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appViewModelModule = module {
    viewModelOf(::AppViewModel)
    /* viewModel {
        AppViewModel(
            ioDispatcher = get(),
            logOutUseCase = get(),
            observeIsConnectedUseCase = get(),
            setIsConnectedUseCase = get()
        )
    }*/
}
