package com.msoula.hobbymatchmaker

import android.app.Application
import com.msoula.hobbymatchmaker.BuildKonfig.FIREBASE_API_KEY
import com.msoula.hobbymatchmaker.BuildKonfig.FIREBASE_APP_ID
import com.msoula.hobbymatchmaker.BuildKonfig.KOTZILLA_KEY
import com.msoula.hobbymatchmaker.core.common.di.coreCommonAndroidSavedStateHandle
import com.msoula.hobbymatchmaker.presentation.navigation.appModule
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import io.kotzilla.sdk.BuildConfig
import io.kotzilla.sdk.analytics.koin.analytics
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

            analytics {
                setApiKey(KOTZILLA_KEY)
                setVersion("1.0.0")
            }
        }
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initFirebase() {
        Firebase.initialize(
            applicationContext,
            options = FirebaseOptions(
                applicationId = FIREBASE_APP_ID,
                apiKey = FIREBASE_API_KEY,
                projectId = "hobby-matchmaker"
            )
        )
    }
}
