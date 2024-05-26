package com.msoula.hobbymatchmaker.core.login.presentation.sign_in.di

import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.SignInNavigationImpl
import com.msoula.hobbymatchmaker.core.navigation.Navigator
import com.msoula.hobbymatchmaker.core.navigation.contracts.SignInNavigation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SignInNavigationModule {

    @Provides
    @Singleton
    fun provideSignInNavigation(navigator: Navigator): SignInNavigation =
        SignInNavigationImpl(navigator)
}
