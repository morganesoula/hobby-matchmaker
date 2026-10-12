package com.msoula.hobbymatchmaker.core.common.di

import com.msoula.hobbymatchmaker.core.common.DefaultDispatcherProvider
import com.msoula.hobbymatchmaker.core.common.DispatcherProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleCommon = module {
    includes(platformModule)

    single<CoroutineDispatcher> { Dispatchers.IO }
    singleOf(::DefaultDispatcherProvider) bind DispatcherProvider::class
    single<FirebaseFirestore>(createdAtStart = true) {
        Firebase.firestore
    }
    single<FirebaseAuth> { Firebase.auth }
    single<Json> {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
            encodeDefaults = true
        }
    }
}

expect val platformModule: org.koin.core.module.Module
