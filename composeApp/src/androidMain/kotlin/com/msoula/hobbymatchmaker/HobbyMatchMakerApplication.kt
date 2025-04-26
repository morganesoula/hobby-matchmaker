package com.msoula.hobbymatchmaker

import android.app.Application
import com.msoula.hobbymatchmaker.core.common.AndroidLogFormatter
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.di.coreCommonAndroidSavedStateHandle
import com.msoula.hobbymatchmaker.presentation.navigation.appModule
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
            appModule()
            modules(
                coreCommonAndroidSavedStateHandle
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
