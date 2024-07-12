package com.msoula.hobbymatchmaker.feature.moviedetail.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MovieDetailScreen(modifier: Modifier = Modifier, title: String, movieId: Long) {
    Text(
        text = "Welcome to the movie detail screen with this movieId: $movieId!",
        color = Color.Blue
    )
}
