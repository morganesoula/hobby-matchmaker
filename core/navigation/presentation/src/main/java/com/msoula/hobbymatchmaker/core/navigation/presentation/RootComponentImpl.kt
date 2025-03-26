package com.msoula.hobbymatchmaker.core.navigation.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.msoula.hobbymatchmaker.core.navigation.domain.AuthComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.MainComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.MovieDetailComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.RootComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.SplashComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.util.coroutineScope
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable

class RootComponentImpl(
    componentContext: ComponentContext,
    private val observeIsConnectedUseCase: ObserveIsConnectedUseCase,
    private val authComponentFactory: (context: ComponentContext, onAuthenticated: () -> Unit) -> AuthComponent,
    private val mainComponentFactory: (ComponentContext, (Long) -> Unit) -> MainComponent,
    private val movieDetailComponentFactory: (ComponentContext, Long) -> MovieDetailComponent,
    private val splashComponentFactory: (ComponentContext, ObserveIsConnectedUseCase) -> SplashComponent
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childSck: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Splash,
            handleBackButton = true,
            childFactory = ::createChild
        )

    @OptIn(DelicateDecomposeApi::class)
    private fun createChild(config: Config, context: ComponentContext): RootComponent.Child =
        when (config) {
            Config.Splash -> RootComponent.Child.Splash {
                val splashComponent = splashComponentFactory(context, observeIsConnectedUseCase)

                splashComponent.isConnected
                    .onEach { isConnected ->
                        when (isConnected) {
                            true -> navigation.replaceAll(Config.Main)
                            false -> navigation.replaceAll(Config.Auth)
                            else -> Unit
                        }
                    }
                    .launchIn(context.coroutineScope())
            }

            Config.Auth -> RootComponent.Child.Auth(
                authComponentFactory(context) {
                    navigation.push(Config.Main)
                }
            )

            Config.Main -> RootComponent.Child.Main(mainComponentFactory(context) { movieId ->
                navigation.push(Config.MovieDetail(movieId))
            })

            is Config.MovieDetail -> RootComponent.Child.MovieDetail(
                movieDetailComponentFactory(context, config.movieId)
            )
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Splash : Config

        @Serializable
        data object Auth : Config

        @Serializable
        data object Main : Config

        @Serializable
        data class MovieDetail(val movieId: Long) : Config
    }
}
