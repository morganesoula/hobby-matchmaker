package com.msoula.hobbymatchmaker.core.common.di

import com.msoula.hobbymatchmaker.core.common.validation.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.common.validation.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.common.validation.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.common.validation.ValidatePasswordUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.dsl.module

val coreModuleCommon = module {
    factory { ValidateEmailUseCase() }
    factory { ValidateNameUseCase() }
    factory { ValidatePasswordUseCase() }

    factory { AuthFormValidationUseCase(get(), get(), get(), get()) }

    single<FirebaseFirestore> {
        Firebase.firestore
    }
}
