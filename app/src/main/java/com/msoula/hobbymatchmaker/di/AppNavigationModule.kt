package com.msoula.hobbymatchmaker.di

import com.msoula.hobbymatchmaker.core.navigation.Navigator
import com.msoula.hobbymatchmaker.core.navigation.contracts.AppNavigation
import com.msoula.hobbymatchmaker.navigation.AppNavigationImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppNavigationModule {

    @Provides
    @Singleton
    fun provideAppScreenNavigation(navigator: Navigator): AppNavigation =
        AppNavigationImpl(navigator)

}
