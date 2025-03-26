package com.msoula.hobbymatchmaker

import android.app.Application
import com.msoula.hobbymatchmaker.core.authentication.data.di.coreModuleAuthenticationData
import com.msoula.hobbymatchmaker.core.authentication.domain.di.coreModuleAuthenticationDomain
import com.msoula.hobbymatchmaker.core.common.AndroidLogFormatter
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.di.coreCommonAndroidSavedStateHandle
import com.msoula.hobbymatchmaker.core.common.di.coreModuleCommon
import com.msoula.hobbymatchmaker.core.database.di.coreModuleDAO
import com.msoula.hobbymatchmaker.core.di.di.coreModuleDI
import com.msoula.hobbymatchmaker.core.login.domain.useCases.di.coreModuleLoginFormValidation
import com.msoula.hobbymatchmaker.core.login.presentation.di.coreModuleSignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.di.coreModuleSignUpViewModel
import com.msoula.hobbymatchmaker.core.network.di.coreModuleNetwork
import com.msoula.hobbymatchmaker.core.session.data.di.coreModuleSessionData
import com.msoula.hobbymatchmaker.core.session.domain.di.coreModuleSessionDomain
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.di.featuresModuleMovieDetailData
import com.msoula.hobbymatchmaker.features.moviedetail.domain.di.featuresModuleMovieDetailDomain
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.di.featuresModuleMovieDetailViewModel
import com.msoula.hobbymatchmaker.features.movies.data.di.featuresModuleMovieData
import com.msoula.hobbymatchmaker.features.movies.domain.di.featuresModuleMovieDomain
import com.msoula.hobbymatchmaker.features.movies.presentation.di.featuresModuleMovieViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class HobbyMatchMakerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogger()
        initFirebase()

        startKoin {
            androidContext(this@HobbyMatchMakerApplication)
            modules(
                listOf(
                    coreModuleNetwork,
                    coreModuleSessionData,
                    coreModuleSessionDomain,
                    coreModuleAuthenticationData,
                    coreModuleAuthenticationDomain,
                    coreModuleCommon,
                    coreModuleDAO,
                    coreModuleDI,
                    coreModuleLoginFormValidation,
                    coreModuleSignInViewModel,
                    coreModuleSignUpViewModel,
                    coreCommonAndroidSavedStateHandle,
                    featuresModuleMovieDetailData,
                    featuresModuleMovieDetailDomain,
                    featuresModuleMovieDetailViewModel,
                    featuresModuleMovieData,
                    featuresModuleMovieDomain,
                    featuresModuleMovieViewModel
                )
            )
        }
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Logger.init(AndroidLogFormatter())
    }

    private fun initFirebase() {
        Firebase.initialize(
            applicationContext,
            options = FirebaseOptions(
                applicationId = BuildConfig.FIREBASE_APP_ID,
                apiKey = BuildConfig.FIREBASE_API_KEY,
                projectId = "hobby-matchmaker"
            )
        )
    }
}
