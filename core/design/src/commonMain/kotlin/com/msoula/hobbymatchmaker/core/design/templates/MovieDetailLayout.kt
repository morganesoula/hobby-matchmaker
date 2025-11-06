package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MovieDetailLayout(
    modifier: Modifier = Modifier,
    snackBarHost: @Composable () -> Unit,
    topBar: @Composable () -> Unit,
    movieImageBackgroundSection: @Composable () -> Unit,
    movieDetailSection: @Composable () -> Unit,
    movieActorsSection: @Composable () -> Unit
) {
    Scaffold(
        snackbarHost = snackBarHost,
        topBar = topBar
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            movieImageBackgroundSection()
            movieDetailSection()
            movieActorsSection()
        }
    }
}
