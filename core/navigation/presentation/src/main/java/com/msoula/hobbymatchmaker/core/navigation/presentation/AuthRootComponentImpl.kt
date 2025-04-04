package com.msoula.hobbymatchmaker.core.navigation.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.msoula.hobbymatchmaker.core.navigation.domain.AuthRootComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.SignInComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.SignUpComponent
import kotlinx.serialization.Serializable

class AuthRootComponentImpl(
    componentContext: ComponentContext,
    private val onAuthenticated: () -> Unit,
    private val signInComponentFactory: (ComponentContext, onSignUpRequested: () -> Unit, onAuthenticated: () -> Unit) -> SignInComponent,
    private val signUpComponentFactory: (ComponentContext, onSignInRequested: () -> Unit, onAuthenticated: () -> Unit) -> SignUpComponent
) : AuthRootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<AuthConfig>()

    override val childStack: Value<ChildStack<*, AuthRootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = AuthConfig.serializer(),
            initialConfiguration = AuthConfig.SignIn,
            handleBackButton = true,
            childFactory = ::createChild
        )

    @OptIn(DelicateDecomposeApi::class)
    private fun createChild(config: AuthConfig, context: ComponentContext): AuthRootComponent.Child =
        when (config) {
            AuthConfig.SignIn -> AuthRootComponent.Child.SignIn(
                signInComponentFactory(
                    context,
                    { navigation.push(AuthConfig.SignUp) },
                    { onAuthenticated() }
                )
            )

            AuthConfig.SignUp -> AuthRootComponent.Child.SignUp(
                signUpComponentFactory(
                    context,
                    { navigation.pop() },
                    { onAuthenticated() }
                )
            )
        }

    @Serializable
    sealed interface AuthConfig {
        @Serializable
        data object SignIn : AuthConfig

        @Serializable
        data object SignUp : AuthConfig
    }
}
