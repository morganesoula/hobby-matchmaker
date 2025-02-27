package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.compose.runtime.Composable

@Composable
expect fun YoutubeComponent(videoId: String)

@Composable
expect fun EnterFullScreen()

@Composable
expect fun ExitFullScreen()

@Composable
expect fun GetScreenHeight(): Float
