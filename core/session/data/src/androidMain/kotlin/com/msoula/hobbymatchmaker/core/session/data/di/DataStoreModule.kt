package com.msoula.hobbymatchmaker.core.session.data.di

import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.SessionLocalDataSourceImpl
import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.createDataStore
import com.msoula.hobbymatchmaker.core.session.domain.dataSources.SessionLocalDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreModuleSessionDataPlatformSpecific = module {
    single { SessionLocalDataSourceImpl(createDataStore(androidContext())) } bind SessionLocalDataSource::class
}
