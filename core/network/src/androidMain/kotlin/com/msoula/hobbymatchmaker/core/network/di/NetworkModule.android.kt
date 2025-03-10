package com.msoula.hobbymatchmaker.core.network.di

import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import org.koin.dsl.module

actual val coreModuleNetworkPlatformSpecific = module {
    single<SignInClient> { Identity.getSignInClient(get()) }
}
