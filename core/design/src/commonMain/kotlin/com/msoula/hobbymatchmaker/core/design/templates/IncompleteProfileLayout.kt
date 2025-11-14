package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun IncompleteProfileLayout() {
    Text(text = "Incomplete profile at the moment", modifier = Modifier.fillMaxSize())
}
