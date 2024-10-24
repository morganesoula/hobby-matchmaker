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
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SocialMediaSignInUseCase
import org.koin.dsl.module

val authenticationDomainModule = module {
    single<AuthenticationRepository> { AuthenticationRepository(get()) }
    factory<LogOutUseCase> { LogOutUseCase(get()) }
    factory<ResetPasswordUseCase> { ResetPasswordUseCase(get()) }
    factory<SignInUseCase> { SignInUseCase(get()) }
    factory<SignUpUseCase> { SignUpUseCase(get()) }
    factory<LinkInWithCredentialUseCase> { LinkInWithCredentialUseCase(get()) }
    factory<SignInWithCredentialUseCase> { SignInWithCredentialUseCase(get()) }
    factory<IsFirstSignInUseCase> { IsFirstSignInUseCase(get()) }
    factory<FetchFirebaseUserInfo> { FetchFirebaseUserInfo(get()) }
    factory<SocialMediaSignInUseCase> { SocialMediaSignInUseCase(get(), get(), get(), get()) }
}
