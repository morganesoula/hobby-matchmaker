package com.msoula.hobbymatchmaker.core.navigation.di

import com.msoula.hobbymatchmaker.core.navigation.Navigator
import com.msoula.hobbymatchmaker.core.navigation.NavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    @Provides
    @Singleton
    fun provideNavigator(): Navigator = NavigatorImpl()
}
