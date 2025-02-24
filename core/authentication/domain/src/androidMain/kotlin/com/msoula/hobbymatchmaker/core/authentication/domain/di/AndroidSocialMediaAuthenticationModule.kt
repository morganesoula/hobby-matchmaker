package com.msoula.hobbymatchmaker.core.authentication.domain.di

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AndroidAuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AndroidAuthenticationRepositoryImpl
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.AndroidSocialMediaSignInUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val androidSocialMediaAuthenticationModule = module {
    singleOf(::AndroidAuthenticationRepositoryImpl) bind  AndroidAuthenticationRepository::class
    singleOf(::AndroidSocialMediaSignInUseCase)
}
