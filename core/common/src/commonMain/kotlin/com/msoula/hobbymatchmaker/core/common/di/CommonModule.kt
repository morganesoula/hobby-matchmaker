package com.msoula.hobbymatchmaker.core.common.di

import com.msoula.hobbymatchmaker.core.common.DefaultDispatcherProvider
import com.msoula.hobbymatchmaker.core.common.DispatcherProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleCommon = module {
    includes(platformModule)

    singleOf(::DefaultDispatcherProvider) bind DispatcherProvider::class
    single<FirebaseFirestore>(createdAtStart = true) {
        Firebase.firestore
    }
}

expect val platformModule: org.koin.core.module.Module
