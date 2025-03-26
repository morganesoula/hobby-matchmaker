package com.msoula.hobbymatchmaker.core.navigation.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.msoula.hobbymatchmaker.core.navigation.domain.AuthComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.SignInComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.SignUpComponent
import kotlinx.serialization.Serializable

class AuthComponentImpl(
    componentContext: ComponentContext,
    private val onAuthenticated: () -> Unit,
    private val signInComponentFactory: (ComponentContext, onSignUpRequested: () -> Unit, onAuthenticated: () -> Unit) -> SignInComponent,
    private val signUpComponentFactory: (ComponentContext, onSignInRequested: () -> Unit, onAuthenticated: () -> Unit) -> SignUpComponent
) : AuthComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, AuthComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.SignIn,
            handleBackButton = true,
            childFactory = ::createChild
        )

    @OptIn(DelicateDecomposeApi::class)
    private fun createChild(config: Config, context: ComponentContext): AuthComponent.Child =
        when (config) {
            Config.SignIn -> AuthComponent.Child.SignIn(
                signInComponentFactory(
                    context,
                    { navigation.push(Config.SignUp) },
                    { onAuthenticated() }
                )
            )

            Config.SignUp -> AuthComponent.Child.SignUp(
                signUpComponentFactory(
                    context,
                    { navigation.push(Config.SignIn) },
                    { onAuthenticated() }
                )
            )
        }

    @Serializable
    sealed interface Config {
        @Serializable
        data object SignIn : Config

        @Serializable
        data object SignUp : Config
    }
}
