package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import com.msoula.hobbymatchmaker.core.navigation.domain.AuthRootComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.MainRootComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.RootComponent
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.SplashScreenContent

@Composable
fun App(
    component: RootComponent,
    socialClients: Map<ProviderType, SocialUIClient>,
    facebookUIClient: FacebookUIClient
) {
    val slotChild by component.currentRootSlot.subscribeAsState()
    val instance = slotChild.child?.instance

    instance?.let { inst ->
        when (inst) {
            is RootComponent.RootChild.SplashFlow -> SplashScreenContent()

            is RootComponent.RootChild.AuthFlow -> {
                val authStack by instance.stack.subscribeAsState()

                Children(stack = authStack) { child ->
                    when (val authScreen = child.instance) {
                        is AuthRootComponent.Child.SignIn -> SignInContent(
                            authScreen.component,
                            socialClients = socialClients,
                            facebookUIClient = facebookUIClient
                        )

                        is AuthRootComponent.Child.SignUp -> SignUpContent(authScreen.component)
                    }
                }
            }

            is RootComponent.RootChild.MainFlow -> {
                val mainStack by instance.stack.subscribeAsState()

                Children(stack = mainStack) { child ->
                    when (val mainScreen = child.instance) {
                        is MainRootComponent.Child.Main -> MovieContent(mainScreen.component)
                        is MainRootComponent.Child.MovieDetail -> MovieDetailContent(mainScreen.component)
                    }
                }
            }
        }
    }
}
