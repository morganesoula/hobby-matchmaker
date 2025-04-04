package com.msoula.hobbymatchmaker.core.navigation.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import com.msoula.hobbymatchmaker.core.navigation.domain.AuthRootComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.MainRootComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.RootComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.SplashRootComponent
import kotlinx.serialization.Serializable

class RootComponentImpl(
    componentContext: ComponentContext,
    private val authComponentFactory: (context: ComponentContext, onAuthenticated: () -> Unit) -> AuthRootComponent,
    private val mainComponentFactory: (ComponentContext, onMovieClicked: (Long) -> Unit, onLogOut: () -> Unit) -> MainRootComponent,
    private val splashComponentFactory: (ComponentContext, onFinished: (Boolean) -> Unit) -> SplashRootComponent
) : RootComponent, ComponentContext by componentContext {

    private val navigation = SlotNavigation<RootConfig>()

    override val currentRootSlot: Value<ChildSlot<*, RootComponent.RootChild>> =
        childSlot(
            source = navigation,
            serializer = RootConfig.serializer(),
            initialConfiguration = { RootConfig.Splash },
            handleBackButton = true,
            childFactory = ::createChild
        )

    private fun createChild(
        config: RootConfig,
        context: ComponentContext
    ): RootComponent.RootChild {
        return when (config) {
            RootConfig.Splash -> RootComponent.RootChild.SplashFlow(
                splashComponentFactory(context) { isConnected ->
                    navigation.activate(if (isConnected) RootConfig.Main else RootConfig.Auth)
                }
            )

            RootConfig.Auth -> RootComponent.RootChild.AuthFlow(
                authComponentFactory(context) {
                    navigation.activate(RootConfig.Main)
                }
            )

            RootConfig.Main -> RootComponent.RootChild.MainFlow(
                mainComponentFactory(
                    context, {},
                    { navigation.activate(RootConfig.Auth) })
            )
        }
    }

    @Serializable
    private sealed interface RootConfig {
        @Serializable
        data object Auth : RootConfig

        @Serializable
        data object Main : RootConfig

        @Serializable
        data object Splash : RootConfig
    }
}
