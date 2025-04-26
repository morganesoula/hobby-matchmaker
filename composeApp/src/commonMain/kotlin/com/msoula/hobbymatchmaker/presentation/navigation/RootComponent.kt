package com.msoula.hobbymatchmaker.presentation.navigation

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.msoula.hobbymatchmaker.core.navigation.domain.RootComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.SignInComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.SignUpComponent
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import presentation.AuthRootComponentImpl
import presentation.MainRootComponentImpl
import presentation.MovieComponentImpl
import presentation.MovieDetailComponentImpl
import presentation.RootComponentImpl
import presentation.SplashRootComponentImpl

fun getRootComponent(): RootComponent {
    val observeIsConnectedUseCase = with(object : KoinComponent {}) {
        get<ObserveIsConnectedUseCase>()
    }

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
                onAuthenticated = onAuthenticated
            )
        },
        mainComponentFactory = { context, _, logOut ->
            MainRootComponentImpl(
                context,
                mainComponentFactory = { ctx, onMovieClicked, onLogOut ->
                    MovieComponentImpl(ctx, onMovieClicked, onLogOut)
                },
                movieDetailComponentFactory = { ctx, id ->
                    MovieDetailComponentImpl(ctx, id)
                },
                onLogout = logOut
            )
        },
        splashComponentFactory = { context, onFinished ->
            SplashRootComponentImpl(
                context,
                observeIsConnectedUseCase = observeIsConnectedUseCase,
                onFinished = onFinished
            )
        }
    )
}
