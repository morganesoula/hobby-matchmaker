package com.msoula.hobbymatchmaker.core.authentication.domain.di

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.IsFirstSignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.LinkInWithCredentialUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignInWithCredentialUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SocialMediaSignInUseCase
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
