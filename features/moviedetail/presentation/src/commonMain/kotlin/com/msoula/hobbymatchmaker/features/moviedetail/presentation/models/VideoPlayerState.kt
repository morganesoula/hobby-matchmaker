package com.msoula.hobbymatchmaker.features.moviedetail.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class VideoPlayerState(
    val videoId: String = "",
    val isVisible: Boolean = false,
    val isLoading: Boolean = false
)