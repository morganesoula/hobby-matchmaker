package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.msoula.hobbymatchmaker.core.design.atoms.CircleWithIcon
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

@Composable
fun GuestProfileHeader(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    titleHeader: String,
    descriptionHeader: String
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .padding(top = CustomSize.Sixteen)
            .padding(horizontal = CustomSize.Sixteen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircleWithIcon(
                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = .5f),
                icon = icon,
                iconTint = MaterialTheme.colorScheme.onSurface
            )

            SpacerHeight8()

            Text(
                text = titleHeader,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            SpacerHeight4()

            Text(
                text = descriptionHeader,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
