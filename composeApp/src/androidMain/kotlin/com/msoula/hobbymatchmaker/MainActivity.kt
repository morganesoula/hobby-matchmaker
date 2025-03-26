package com.msoula.hobbymatchmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.credentials.CredentialManager
import com.arkivanov.decompose.defaultComponentContext
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchmakerTheme
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AndroidGoogleUIClient
import com.msoula.hobbymatchmaker.core.navigation.domain.SignInComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.SignUpComponent
import com.msoula.hobbymatchmaker.core.navigation.presentation.AuthComponentImpl
import com.msoula.hobbymatchmaker.core.navigation.presentation.MovieComponentImpl
import com.msoula.hobbymatchmaker.core.navigation.presentation.MovieDetailComponentImpl
import com.msoula.hobbymatchmaker.core.navigation.presentation.RootComponentImpl
import com.msoula.hobbymatchmaker.core.navigation.presentation.SplashComponentImpl
import com.msoula.hobbymatchmaker.presentation.navigation.App
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {

    //private val auth: FirebaseAuth by inject()
    //private lateinit var authStateListener: AuthStateListener
    private val googleUIClient = AndroidGoogleUIClient(CredentialManager.create(this), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootComponent = RootComponentImpl(
            componentContext = defaultComponentContext(),
            observeIsConnectedUseCase = get(),
            authComponentFactory = { context, onAuthenticated ->
                AuthComponentImpl(
                    context,
                    onAuthenticated,
                    signInComponentFactory = { _, onSignUp, onAuth ->
                        object : SignInComponent {
                            override fun onSignUpClicked() = onSignUp()
                            override fun onAuthenticated() = onAuth()
                        }
                    },
                    signUpComponentFactory = { _, onSignIn, onAuth ->
                        object : SignUpComponent {
                            override fun onAuthenticated() = onAuth()
                            override fun onSignInClicked() = onSignIn()
                        }
                    })
            },
            mainComponentFactory = { context, navigateToDetail ->
                MovieComponentImpl(context, navigateToDetail)
            },
            movieDetailComponentFactory = { context, movieId ->
                MovieDetailComponentImpl(context, movieId)
            },
            splashComponentFactory = { context, observeIsConnectedUseCase ->
                SplashComponentImpl(context, observeIsConnectedUseCase = observeIsConnectedUseCase)
            }
        )

        setContent {
            HobbyMatchmakerTheme {
                App(component = rootComponent, googleUIClient = googleUIClient)
            }
        }
    }

    /* override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun setAuthenticationListener() {
        authStateListener = AuthStateListener { _ ->
            //If you want to add some data to the session, you can do it here
        }
    } */
}
