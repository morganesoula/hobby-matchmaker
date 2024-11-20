package com.msoula.hobbymatchmaker.presentation.di

import com.msoula.hobbymatchmaker.presentation.AppViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appViewModelModule = module {
    viewModelOf(::AppViewModel)
}
