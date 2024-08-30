package com.msoula.hobbymatchmaker.core.session.data.di

import com.msoula.hobbymatchmaker.core.session.data.data_sources.local.SessionLocalDataSourceImpl
import com.msoula.hobbymatchmaker.core.session.domain.data_sources.SessionLocalDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sessionDataModule = module {
    factory<SessionLocalDataSource> { SessionLocalDataSourceImpl(androidContext()) }
}
