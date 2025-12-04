package com.msoula.hobbymatchmaker.features.social.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SocialContent(
    modifier: Modifier = Modifier,
    socialViewModel: SocialViewModel,
    onNavigate: (String) -> Unit
) {
    Column {
        Text("Welcome to Social")
        Button(onClick = { onNavigate("movies") }) {
            Text("Lets go back")
        }
    }
}
