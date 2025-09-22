package com.msoula.hobbymatchmaker.tests.modules

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.data.repositories.AuthenticationRepositoryImpl
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.SessionLocalDataSource
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.SessionRemoteDataSource
import com.msoula.hobbymatchmaker.core.session.data.repositories.SessionRepositoryImpl
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import com.msoula.hobbymatchmaker.tests.fakes.FakeAuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.tests.fakes.FakeSessionLocalDataSource
import com.msoula.hobbymatchmaker.tests.fakes.FakeSessionRemoteDataSource
import com.msoula.hobbymatchmaker.tests.helpers.PlainErrorMessageMapper
import org.koin.dsl.bind
import org.koin.dsl.module

val signUpTestModule = module {
    single { FakeAuthenticationRemoteDataSource() } bind AuthenticationRemoteDataSource::class
    single { FakeSessionRemoteDataSource() } bind SessionRemoteDataSource::class
    single { FakeSessionLocalDataSource() } bind SessionLocalDataSource::class

    // Repositories (REAL)
    single { AuthenticationRepositoryImpl(get()) } bind AuthenticationRepository::class
    single { SessionRepositoryImpl(get(), get()) } bind SessionRepository::class

    // Use cases (REAL)
    factory { CreateUserUseCase(get()) }
    factory { SignUpUseCase(get(), get()) }

    // Mapper override
    single<ErrorMessageMapper> { PlainErrorMessageMapper }
}
