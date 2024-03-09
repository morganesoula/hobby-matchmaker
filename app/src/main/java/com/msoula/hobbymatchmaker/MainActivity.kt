package com.msoula.hobbymatchmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.msoula.di.navigation.Navigator
import com.msoula.hobbymatchmaker.app.HobbyMatchMakerApp
import com.msoula.movies.presentation.MovieViewModel
import com.msoula.theme.HobbyMatchmakerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    private val movieViewModel: MovieViewModel by viewModels<MovieViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                movieViewModel.displayingData.value
            }
        }

        setContent {
            HobbyMatchmakerTheme {
                HobbyMatchMakerApp(navigator)
            }
        }
    }
}
