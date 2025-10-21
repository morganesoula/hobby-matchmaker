package com.msoula.hobbymatchmaker.core.common.di

import com.msoula.hobbymatchmaker.core.common.DefaultErrorMessageMapper
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleCommon = module {
    single { DefaultErrorMessageMapper } bind ErrorMessageMapper::class
    single<FirebaseFirestore> {
        Firebase.firestore
    }
}
