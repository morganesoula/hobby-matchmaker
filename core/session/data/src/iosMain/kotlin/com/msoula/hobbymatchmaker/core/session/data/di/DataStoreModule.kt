package com.msoula.hobbymatchmaker.core.session.data.di

import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.SessionLocalDataSourceImpl
import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.createDataStore
import com.msoula.hobbymatchmaker.core.session.domain.dataSources.SessionLocalDataSource
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreModuleSessionDataPlatformSpecific = module {
    single { SessionLocalDataSourceImpl(createDataStore()) } bind SessionLocalDataSource::class
}
