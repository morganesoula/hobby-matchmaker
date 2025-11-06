package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

@Composable
private fun MetaPill(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                shape = RoundedCornerShape(CustomSize.Sixteen)
            )
            .padding(horizontal = CustomSize.Eight, vertical = CustomSize.Eight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun MovieTitleMetaPill(
    modifier: Modifier = Modifier,
    title: String
) {
    val titleShadow = Shadow(
        color = MaterialTheme.colorScheme.background.copy(alpha = .35f),
        offset = Offset(0f, 1.5f),
        blurRadius = 3f
    )

    MetaPill(modifier = modifier.padding(top = 6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                shadow = titleShadow
            )
        )
    }
}

@Composable
fun MovieInformationMetaPill(
    modifier: Modifier = Modifier,
    releaseDate: String,
    genres: List<String>,
    duration: String
) {
    MetaPill {
        Text(
            text = "$releaseDate · ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = genres.take(3).joinToString(" · "),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = " · $duration",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
