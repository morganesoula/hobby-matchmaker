package com.msoula.hobbymatchmaker.core.navigation.domain

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

interface AuthRootComponent {
    val childStack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class SignIn(val component: SignInComponent) : Child
        data class SignUp(val component: SignUpComponent) : Child
    }
}
