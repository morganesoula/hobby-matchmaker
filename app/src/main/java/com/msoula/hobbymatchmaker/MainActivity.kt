package com.msoula.hobbymatchmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.msoula.di.navigation.Navigator
import com.msoula.hobbymatchmaker.app.HobbyMatchMakerApp
import com.msoula.theme.HobbyMatchmakerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HobbyMatchmakerTheme {
                HobbyMatchMakerApp(navigator)
            }
        }
    }
}
