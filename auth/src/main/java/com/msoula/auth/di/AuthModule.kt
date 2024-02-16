package com.msoula.auth.di

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.msoula.auth.data.AuthRepositoryImpl
import com.msoula.auth.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideOneTapClient(
        @ApplicationContext context: Context,
    ): SignInClient = Identity.getSignInClient(context)

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
    ): AuthRepository =
        AuthRepositoryImpl(
            auth = provideFirebaseAuth(),
            oneTapClient = provideOneTapClient(context),
        )
}
