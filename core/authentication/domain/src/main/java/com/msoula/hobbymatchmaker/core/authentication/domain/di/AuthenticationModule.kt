package com.msoula.hobbymatchmaker.core.authentication.domain.di

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.LoginWithSocialMediaUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignUpUseCase
import org.koin.dsl.module

val authenticationDomainModule = module {
    single<AuthenticationRepository> { AuthenticationRepository(get()) }
    factory<LogOutUseCase> { LogOutUseCase(get()) }
    factory<ResetPasswordUseCase> { ResetPasswordUseCase(get()) }
    factory<SignInUseCase> { SignInUseCase(get()) }
    factory<SignUpUseCase> { SignUpUseCase(get()) }
    factory<LoginWithSocialMediaUseCase> { LoginWithSocialMediaUseCase(get()) }
}
