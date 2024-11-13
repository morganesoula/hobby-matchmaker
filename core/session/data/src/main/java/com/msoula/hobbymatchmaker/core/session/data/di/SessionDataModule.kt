package com.msoula.hobbymatchmaker.core.session.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.SessionLocalDataSourceImpl
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.SessionRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.core.session.domain.dataSources.SessionLocalDataSource
import com.msoula.hobbymatchmaker.core.session.domain.dataSources.SessionRemoteDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sessionDataModule = module {
    single<FirebaseFirestore> { FirebaseFirestore.getInstance() }
    singleOf(::SessionLocalDataSourceImpl) bind SessionLocalDataSource::class
    singleOf(::SessionRemoteDataSourceImpl) bind SessionRemoteDataSource::class
}
