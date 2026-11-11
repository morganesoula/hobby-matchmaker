package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapArrowRight
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize

@Composable
fun SettingsSectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(horizontal = CustomSize.Sixteen)
    )
}

@Composable
fun SettingsActionCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    isDestructive: Boolean = false,
    showArrow: Boolean = false
) {
    val accent = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.Sixteen),
        shape = RoundedCornerShape(CustomSize.Sixteen),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(1.dp, accent.copy(alpha = .3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CustomSize.Sixteen),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
        ) {
            Box(
                modifier = Modifier
                    .size(CustomSize.FortyEight)
                    .background(accent.copy(alpha = .15f), RoundedCornerShape(CustomSize.Sixteen)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(IconSize.TwentyFour)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(CustomSize.Four)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDestructive) accent else MaterialTheme.colorScheme.onSurface
                )
                description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (showArrow) {
                Icon(
                    imageVector = BootstrapArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(IconSize.TwentyFour)
                )
            }
        }
    }
}
