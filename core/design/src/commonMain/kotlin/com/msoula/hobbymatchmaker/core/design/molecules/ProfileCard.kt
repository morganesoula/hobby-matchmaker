package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.RectangleWithIcon
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth8
import com.msoula.hobbymatchmaker.core.design.theme.CustomFontSize
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

@Composable
fun FeatureProfileCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    titleFeature: String,
    descriptionFeature: String
) {
    GenericCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.TwentyFour),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(CustomSize.Sixteen),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RectangleWithIcon(
                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = .2f),
                icon = icon,
                iconTint = MaterialTheme.colorScheme.primary
            )

            SpacerWidth8()

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = titleFeature,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = CustomFontSize.Sixteen,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = descriptionFeature,
                    fontSize = CustomFontSize.Twelve,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .85f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun FeatureProfileCardWithButton(
    modifier: Modifier = Modifier,
    titleFeature: String,
    descriptionFeature: String,
    buttonText: String,
    onClick: () -> Unit
) {
    GenericCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.TwentyFour),
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = .8f)
    ) {
        Column(
            modifier = modifier.fillMaxSize().padding(CustomSize.Eight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titleFeature,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = descriptionFeature,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
            SpacerHeight4()
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
            ) {
                Text(
                    text = buttonText,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
