package com.msoula.hobbymatchmaker.core.navigation.domain

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

interface SplashRootComponent {
    val dummyStack: Value<ChildStack<*, *>>
}
