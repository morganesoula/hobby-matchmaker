package com.msoula.hobbymatchmaker

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.msoula.di.navigation.Navigator
import com.msoula.hobbymatchmaker.app.HobbyMatchMakerApp
import com.msoula.theme.HobbyMatchmakerTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val directory = File(
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "movie_poster"
        )
        if (!directory.exists()) {
            directory.mkdirs()
        }

        setContent {
            HobbyMatchmakerTheme {
                HobbyMatchMakerApp(navigator)
            }
        }
    }
}
