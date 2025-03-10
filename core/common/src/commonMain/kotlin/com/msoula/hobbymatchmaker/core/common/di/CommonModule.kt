package com.msoula.hobbymatchmaker.core.common.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.dsl.module

val coreModuleCommon = module {
    single<FirebaseFirestore> {
        Firebase.firestore
    }
}
