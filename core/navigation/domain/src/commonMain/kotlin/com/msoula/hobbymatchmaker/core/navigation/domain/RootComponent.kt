package com.msoula.hobbymatchmaker.core.navigation.domain

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

interface RootComponent {
    val currentRootSlot: Value<ChildSlot<*, RootChild>>

    sealed interface RootChild {
        val stack: Value<ChildStack<*, *>>

        data class AuthFlow(val component: AuthRootComponent) : RootChild {
            override val stack: Value<ChildStack<*, *>>
                get() = component.childStack
        }

        data class MainFlow(val component: MainRootComponent) : RootChild {
            override val stack: Value<ChildStack<*, *>>
                get() = component.childStack
        }

        data class SplashFlow(val component: SplashRootComponent) : RootChild {
            override val stack: Value<ChildStack<*, *>>
                get() = component.dummyStack
        }
    }
}
