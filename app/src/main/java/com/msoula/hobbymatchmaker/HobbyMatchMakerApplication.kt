package com.msoula.hobbymatchmaker

import android.app.Application
import com.facebook.FacebookSdk
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HobbyMatchMakerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
