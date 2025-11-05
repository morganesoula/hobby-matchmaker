package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun MovieLayout(content: @Composable () -> Unit) {
    Scaffold {
        content()
    }
}
