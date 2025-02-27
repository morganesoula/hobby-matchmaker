package com.msoula.hobbymatchmaker.features.moviedetail.presentation.di

import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import org.koin.dsl.module

val movieDetailViewModelModule = module {
    single { MovieDetailViewModel(get(), get(), get(), get()) }
}
