package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun YoutubeComponent(modifier: Modifier, videoId: String, onClose: () -> Unit)

@Composable
expect fun GetScreenHeight(): Float
