package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

@Composable
fun ActorIcon(
    modifier: Modifier = Modifier,
    contentDescription: String,
    imageVector: ImageVector? = null
) {
    Icon(
        imageVector = imageVector ?: Icons.Default.People,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.size(CustomSize.ThirtyTwo)
    )
}
