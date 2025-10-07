package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchMakerTheme
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
    HobbyMatchMakerTheme {
        /* Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .border(2.dp, Color.Magenta)
            ) {
                Text("This a text to test screen size on iPhone", color = Color.White)
            }
        }*/

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

                    Children(
                        stack = mainStack,
                        animation = stackAnimation(fade() + slide())
                    ) { child ->
                        when (val mainScreen = child.instance) {
                            is MainRootComponent.Child.Main -> MovieContent(mainScreen.component)
                            is MainRootComponent.Child.MovieDetail -> MovieDetailContent(mainScreen.component)
                        }
                    }
                }
            }
        }
    }
}
