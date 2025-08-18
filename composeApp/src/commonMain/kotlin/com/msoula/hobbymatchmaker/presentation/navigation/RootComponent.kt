package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.navigation.domain.RootComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.SignInComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.SignUpComponent
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SyncLocalFavoritesToCloudUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import presentation.AuthRootComponentImpl
import presentation.MainRootComponentImpl
import presentation.MovieComponentImpl
import presentation.MovieDetailComponentImpl
import presentation.RootComponentImpl
import presentation.SplashRootComponentImpl

@Composable
fun getRootComponent(): RootComponent {
    val observeIsConnectedUseCase: ObserveIsConnectedUseCase =
        getKoin().get<ObserveIsConnectedUseCase>()
    val syncLocalFavoritesToCloudUseCase: SyncLocalFavoritesToCloudUseCase =
        getKoin().get<SyncLocalFavoritesToCloudUseCase>()

    val appScope = rememberCoroutineScope()

    return RootComponentImpl(
        componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
        authComponentFactory = { context, onAuthenticated ->
            AuthRootComponentImpl(
                context,
                signInComponentFactory = { _, onSignUp, onAuth ->
                    object : SignInComponent {
                        override fun onSignUpClicked() = onSignUp()
                        override fun onAuthenticated() = onAuth()
                    }
                },
                signUpComponentFactory = { _, onSignIn, onAuth ->
                    object : SignUpComponent {
                        override fun onSignInClicked() = onSignIn()
                        override fun onAuthenticated() = onAuth()
                    }
                },
                onAuthenticated = {
                    appScope.launch {
                        syncLocalFavoritesToCloudUseCase(Parameters.None).collect()
                    }

                    onAuthenticated()
                }
            )
        },
        mainComponentFactory = { context, _, logOut ->
            MainRootComponentImpl(
                context,
                mainComponentFactory = { ctx, onMovieClicked, onLogOut ->
                    MovieComponentImpl(ctx, onMovieClicked, onLogOut)
                },
                movieDetailComponentFactory = { ctx, id, onMovieDetailBackPressed ->
                    MovieDetailComponentImpl(ctx, id, onMovieDetailBackPressed)
                },
                onLogout = logOut
            )
        },
        splashComponentFactory = { context, onFinished ->
            SplashRootComponentImpl(
                context,
                observeIsConnectedUseCase = observeIsConnectedUseCase,
                onFinished = { isConnected ->
                    if (isConnected) {
                        appScope.launch {
                            syncLocalFavoritesToCloudUseCase(Parameters.None).collect()
                        }
                    }
                    onFinished(isConnected)
                }
            )
        }
    )
}
