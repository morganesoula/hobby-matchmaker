package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClient
import com.msoula.hobbymatchmaker.core.navigation.domain.AuthComponent

@Composable
fun AuthContent(component: AuthComponent, googleUIClient: GoogleUIClient) {
    Children(stack = component.childStack) { child ->
        when (val instance = child.instance) {
            is AuthComponent.Child.SignIn -> SignInContent(instance.component, googleUIClient)
            is AuthComponent.Child.SignUp -> SignUpContent(instance.component)
        }
    }
}
