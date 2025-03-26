package com.msoula.hobbymatchmaker.core.navigation.domain

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

interface RootComponent {
    val childSck: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Splash(val onFinish: () -> Unit) : Child
        data class Auth(val component: AuthComponent) : Child
        data class Main(val component: MainComponent) : Child
        data class MovieDetail(val component: MovieDetailComponent) : Child
    }
}
