package com.msoula.hobbymatchmaker.core.login.presentation.sign_up.di

import com.msoula.hobbymatchmaker.core.login.presentation.sign_up.SignUpNavigationImpl
import com.msoula.hobbymatchmaker.core.navigation.Navigator
import com.msoula.hobbymatchmaker.core.navigation.contracts.SignUpNavigation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SignUpNavigationModule {

    @Provides
    @Singleton
    fun provideSignUpNavigation(navigator: Navigator): SignUpNavigation {
        return SignUpNavigationImpl(navigator)
    }
}
