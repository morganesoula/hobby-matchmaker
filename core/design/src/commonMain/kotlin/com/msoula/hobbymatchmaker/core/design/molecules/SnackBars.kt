package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

@Composable
fun ErrorSnackBars(
    modifier: Modifier = Modifier,
    text: String
) {
    Snackbar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(CustomSize.Eight)
    ) {
        Text(text = text)
    }
}
