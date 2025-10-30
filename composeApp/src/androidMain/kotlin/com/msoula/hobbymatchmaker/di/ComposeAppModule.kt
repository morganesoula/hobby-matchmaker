package com.msoula.hobbymatchmaker.di

import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import org.koin.dsl.module

val seeIfWeKeepItModule = module {
    single<SignInClient> { Identity.getSignInClient(get()) }
}
