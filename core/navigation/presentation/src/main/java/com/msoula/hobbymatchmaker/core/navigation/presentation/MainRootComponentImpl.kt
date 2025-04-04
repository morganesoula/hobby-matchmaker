package com.msoula.hobbymatchmaker.core.navigation.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.msoula.hobbymatchmaker.core.navigation.domain.MainComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.MainRootComponent
import com.msoula.hobbymatchmaker.core.navigation.domain.MovieDetailComponent
import kotlinx.serialization.Serializable

class MainRootComponentImpl(
    componentContext: ComponentContext,
    private val mainComponentFactory: (ComponentContext, onMovieClicked: (Long) -> Unit, onLogout: () -> Unit) -> MainComponent,
    private val movieDetailComponentFactory: (context: ComponentContext, movieId: Long) -> MovieDetailComponent,
    private val onLogout: () -> Unit
) : MainRootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<MainConfig>()

    override val childStack: Value<ChildStack<*, MainRootComponent.Child>> = childStack(
        source = navigation,
        serializer = MainConfig.serializer(),
        initialConfiguration = MainConfig.Main,
        handleBackButton = true,
        childFactory = ::createChild
    )

    @OptIn(DelicateDecomposeApi::class)
    private fun createChild(
        config: MainConfig,
        context: ComponentContext
    ): MainRootComponent.Child =
        when (config) {
            MainConfig.Main -> MainRootComponent.Child.Main(
                mainComponentFactory(
                    context,
                    { id -> navigation.push(MainConfig.MovieDetail(id)) },
                    onLogout
                )
            )

            is MainConfig.MovieDetail -> MainRootComponent.Child.MovieDetail(
                movieDetailComponentFactory(
                    context,
                    config.movieId
                )
            )
        }
}

@Serializable
private sealed interface MainConfig {
    @Serializable
    data object Main : MainConfig

    @Serializable
    data class MovieDetail(val movieId: Long) : MainConfig
}
