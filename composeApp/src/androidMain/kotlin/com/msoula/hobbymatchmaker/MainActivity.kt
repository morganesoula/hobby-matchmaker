package com.msoula.hobbymatchmaker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.credentials.CredentialManager
import com.arkivanov.decompose.defaultComponentContext
import com.facebook.CallbackManager
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchmakerTheme
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AndroidFacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AndroidGoogleUIClient
import com.msoula.hobbymatchmaker.core.navigation.domain.SignInComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.SignUpComponent
import com.msoula.hobbymatchmaker.core.navigation.presentation.AuthRootComponentImpl
import com.msoula.hobbymatchmaker.core.navigation.presentation.MainRootComponentImpl
import com.msoula.hobbymatchmaker.core.navigation.presentation.MovieComponentImpl
import com.msoula.hobbymatchmaker.core.navigation.presentation.MovieDetailComponentImpl
import com.msoula.hobbymatchmaker.core.navigation.presentation.RootComponentImpl
import com.msoula.hobbymatchmaker.core.navigation.presentation.SplashRootComponentImpl
import com.msoula.hobbymatchmaker.presentation.navigation.App
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {

    private val googleUIClient = AndroidGoogleUIClient(CredentialManager.create(this), this)
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val facebookUIClient = AndroidFacebookUIClient({ this }, callbackManager)

        val rootComponent = RootComponentImpl(
            componentContext = defaultComponentContext(),
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
                    observeIsConnectedUseCase = get(),
                    onFinished = onFinished
                )
            }
        )

        setContent {
            HobbyMatchmakerTheme {
                App(
                    component = rootComponent,
                    googleUIClient = googleUIClient,
                    facebookUIClient = facebookUIClient
                )
            }
        }
    }

    @Deprecated(
        "This method has been deprecated in favor of using the Activity Result API\n" +
            "which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n" +
            " contracts for common intents available in\n" +
            "{@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n" +
            "testing, and allow receiving results in separate, testable classes independent from your\n" +
            "activity. Use\n" +
            "{@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n" +
            "with the appropriate {@link ActivityResultContract} and handling the result in the\n" +
            "{@link ActivityResultCallback#onActivityResult(Object) callback}."
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.d("Return from onActivityResult")
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
