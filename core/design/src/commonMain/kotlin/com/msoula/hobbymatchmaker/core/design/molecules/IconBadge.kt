package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize

@Composable
fun IconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val ringShape = RoundedCornerShape(CustomSize.TwentyFour)
    val badgeShape = RoundedCornerShape(CustomSize.Sixteen)

    Box(
        modifier = modifier
            .background(primary.copy(alpha = .05f), ringShape)
            .padding(CustomSize.Eight),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(primary.copy(alpha = .15f), primary.copy(alpha = .05f))
                    ),
                    badgeShape
                )
                .border(1.dp, primary.copy(alpha = .3f), badgeShape)
                .padding(CustomSize.Sixteen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primary,
                modifier = Modifier.size(IconSize.TwentyFour)
            )
        }
    }
}
