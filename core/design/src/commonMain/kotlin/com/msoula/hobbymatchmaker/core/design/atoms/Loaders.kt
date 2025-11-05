package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoadingDataLoader(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(modifier = modifier)
}
