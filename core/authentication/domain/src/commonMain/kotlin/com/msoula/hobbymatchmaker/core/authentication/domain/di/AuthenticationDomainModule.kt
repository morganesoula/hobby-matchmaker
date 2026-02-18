package com.msoula.hobbymatchmaker.core.authentication.domain.di

import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfoUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.IsFirstSignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LinkInWithCredentialUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInWithSocialProviderUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val coreModuleAuthenticationDomain = module {
    factoryOf(::LogOutUseCase)
    factoryOf(::ResetPasswordUseCase)
    factoryOf(::SignInUseCase)
    factoryOf(::SignUpUseCase)
    factoryOf(::LinkInWithCredentialUseCase)
    factoryOf(::IsFirstSignInUseCase)
    factoryOf(::FetchFirebaseUserInfoUseCase)
    factoryOf(::SignInWithSocialProviderUseCase)
}
