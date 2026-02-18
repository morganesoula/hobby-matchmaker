package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.msoula.hobbymatchmaker.core.design.atoms.EngagingButton
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize

@Composable
fun NoDataCard(
    modifier: Modifier = Modifier,
    noDataTitle: String,
    noDataText: String,
    noDataBtnText: String,
    icon: ImageVector,
    onNoDataButtonClicked: () -> Unit
) {
    Card(
        modifier = modifier.padding(start = CustomSize.Sixteen, end = CustomSize.Sixteen),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(CustomSize.Sixteen)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(CustomSize.Sixteen),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = .1f),
                        CircleShape
                    )
                    .size(IconSize.SixtyFour)
                    .padding(CustomSize.Sixteen)
            )
            SpacerHeight16()
            Text(
                text = noDataTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            SpacerHeight8()
            Text(
                text = noDataText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .8f),
                minLines = 2,
                textAlign = TextAlign.Center
            )
            SpacerHeight16()
            EngagingButton(
                text = noDataBtnText,
                onClick = { onNoDataButtonClicked() }
            )
        }
    }
}
