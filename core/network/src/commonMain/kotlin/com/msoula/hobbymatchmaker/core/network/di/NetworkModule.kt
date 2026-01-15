package com.msoula.hobbymatchmaker.core.network.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModuleNetwork = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
    includes(coreModuleNetworkPlatformSpecific)
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

expect val coreModuleNetworkPlatformSpecific: Module
