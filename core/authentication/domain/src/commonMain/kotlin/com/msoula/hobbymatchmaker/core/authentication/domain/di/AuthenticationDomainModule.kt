package com.msoula.hobbymatchmaker.core.authentication.domain.di

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepositoryImpl
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.IsFirstSignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LinkInWithCredentialUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInWithCredentialUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.UnifiedSignInUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleAuthenticationDomain = module {
    singleOf(::AuthenticationRepositoryImpl) bind AuthenticationRepository::class
    factoryOf(::LogOutUseCase)
    singleOf(::ResetPasswordUseCase)
    factoryOf(::SignInUseCase)
    factoryOf(::SignUpUseCase)
    factoryOf(::LinkInWithCredentialUseCase)
    factoryOf(::SignInWithCredentialUseCase)
    factoryOf(::IsFirstSignInUseCase)
    factoryOf(::FetchFirebaseUserInfo)
    factoryOf(::UnifiedSignInUseCase)
}
