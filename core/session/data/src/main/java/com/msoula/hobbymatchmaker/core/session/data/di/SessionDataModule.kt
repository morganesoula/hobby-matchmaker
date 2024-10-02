package com.msoula.hobbymatchmaker.core.session.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.msoula.hobbymatchmaker.core.session.data.data_sources.local.SessionLocalDataSourceImpl
import com.msoula.hobbymatchmaker.core.session.data.data_sources.remote.SessionRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.core.session.domain.data_sources.SessionLocalDataSource
import com.msoula.hobbymatchmaker.core.session.domain.data_sources.SessionRemoteDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sessionDataModule = module {
    single<FirebaseFirestore> { FirebaseFirestore.getInstance() }
    single<SessionLocalDataSource> { SessionLocalDataSourceImpl(androidContext()) }
    single<SessionRemoteDataSource> { SessionRemoteDataSourceImpl(get()) }
}
