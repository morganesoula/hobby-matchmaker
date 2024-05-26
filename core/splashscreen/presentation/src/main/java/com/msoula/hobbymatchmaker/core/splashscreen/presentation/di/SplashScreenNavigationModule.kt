package com.msoula.hobbymatchmaker.core.splashscreen.presentation.di

import com.msoula.hobbymatchmaker.core.navigation.Navigator
import com.msoula.hobbymatchmaker.core.navigation.contracts.SplashScreenNavigation
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.SplashScreenNavigationImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SplashScreenNavigationModule {

    @Provides
    @Singleton
    fun provideSplashScreenNavigation(navigator: Navigator): SplashScreenNavigation =
        SplashScreenNavigationImpl(navigator)
}
