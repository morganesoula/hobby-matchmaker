package com.msoula.hobbymatchmaker.core.navigation.domain

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

interface MainRootComponent {
    val childStack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Main(val component: MainComponent) : Child
        data class MovieDetail(val component: MovieDetailComponent) : Child
    }
}
