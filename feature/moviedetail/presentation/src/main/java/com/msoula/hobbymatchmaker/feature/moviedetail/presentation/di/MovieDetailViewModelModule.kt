package com.msoula.hobbymatchmaker.feature.moviedetail.presentation.di

import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.MovieDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val movieDetailViewModelModule = module {
    viewModel {
        MovieDetailViewModel(get(), get(), get(), get())
    }
}
