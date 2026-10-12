package com.msoula.hobbymatchmaker

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseOptions
import com.google.firebase.initialize
import com.msoula.hobbymatchmaker.di.coreCommonAndroidSavedStateHandle
import com.msoula.hobbymatchmaker.presentation.navigation.appModule
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
                appModule() + coreCommonAndroidSavedStateHandle
            )
        }
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initFirebase() {
        Firebase.initialize(
            context = applicationContext,
            options = FirebaseOptions.Builder()
                .setApiKey(BuildConfig.FIREBASE_API_KEY)
                .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                .setProjectId("hobby-matchmaker")
                .build()
        )
    }
}
