package com.msoula.hobbymatchmaker.core.di.module

import com.msoula.hobbymatchmaker.core.di.data.StringResourcesProviderImpl
import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val resourceModule = module {
    single<StringResourcesProvider> { StringResourcesProviderImpl(androidContext()) }
}
