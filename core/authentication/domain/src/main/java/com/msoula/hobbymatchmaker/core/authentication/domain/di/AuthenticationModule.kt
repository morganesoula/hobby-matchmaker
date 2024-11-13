package com.msoula.hobbymatchmaker.core.authentication.domain.di

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.IsFirstSignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LinkInWithCredentialUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInWithCredentialUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SocialMediaSignInBisUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authenticationDomainModule = module {
    singleOf(::AuthenticationRepository)
    singleOf(::AuthenticationRepository)
    factoryOf(::LogOutUseCase)
    singleOf(::ResetPasswordUseCase)
    factoryOf(::SignInUseCase)
    factoryOf(::SignUpUseCase)
    factoryOf(::LinkInWithCredentialUseCase)
    factoryOf(::SignInWithCredentialUseCase)
    factoryOf(::IsFirstSignInUseCase)
    factoryOf(::FetchFirebaseUserInfo)
    factoryOf(::SocialMediaSignInBisUseCase)
}
