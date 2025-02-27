package com.msoula.hobbymatchmaker

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.msoula.hobbymatchmaker.core.authentication.data.di.authenticationDataModule
import com.msoula.hobbymatchmaker.core.authentication.domain.di.authenticationDomainModule
import com.msoula.hobbymatchmaker.core.common.databaseModule
import com.msoula.hobbymatchmaker.core.di.di.authValidationDataUseCaseModule
import com.msoula.hobbymatchmaker.core.di.di.dispatcherModule
import com.msoula.hobbymatchmaker.core.di.di.stringResourcesProviderModule
import com.msoula.hobbymatchmaker.core.login.domain.useCases.di.loginFormValidationModule
import com.msoula.hobbymatchmaker.core.login.presentation.di.signInViewModelModule
import com.msoula.hobbymatchmaker.core.login.presentation.di.signUpViewModelModule
import com.msoula.hobbymatchmaker.core.network.di.networkModule
import com.msoula.hobbymatchmaker.core.session.data.di.sessionDataModule
import com.msoula.hobbymatchmaker.core.session.domain.di.sessionDomainModule
import com.msoula.hobbymatchmaker.database.di.localDatabaseModule
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.di.movieDetailDataModule
import com.msoula.hobbymatchmaker.features.moviedetail.domain.di.movieDetailDomainModule
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.di.movieDetailViewModelModule
import com.msoula.hobbymatchmaker.features.movies.data.di.movieDataModule
import com.msoula.hobbymatchmaker.features.movies.domain.di.movieDomainModule
import com.msoula.hobbymatchmaker.features.movies.presentation.di.movieViewModelModule
import com.msoula.hobbymatchmaker.presentation.di.appViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HobbyMatchMakerApplication : Application() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        startKoin {
            appContext = applicationContext

            androidContext(this@HobbyMatchMakerApplication)
            modules(
                listOf(
                    dispatcherModule,
                    networkModule,
                    loginFormValidationModule,
                    stringResourcesProviderModule,
                    authValidationDataUseCaseModule,
                    databaseModule,
                    authenticationDataModule,
                    authenticationDomainModule,
                    movieDataModule,
                    movieDomainModule,
                    movieDetailDataModule,
                    movieDetailDomainModule,
                    sessionDataModule,
                    sessionDomainModule,
                    appViewModelModule,
                    signInViewModelModule,
                    signUpViewModelModule,
                    movieViewModelModule,
                    movieDetailViewModelModule,
                    localDatabaseModule
                )
            )
        }
    }
}
