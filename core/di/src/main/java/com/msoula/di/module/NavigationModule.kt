package com.msoula.di.module

import com.msoula.di.navigation.Navigator
import com.msoula.di.navigation.NavigatorImpl
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
    fun providesNavigator(): Navigator = NavigatorImpl()
}