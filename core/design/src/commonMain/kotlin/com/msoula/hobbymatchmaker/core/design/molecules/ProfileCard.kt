package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.theme.CustomFontSize

@Composable
fun FeatureProfileCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String? = null,
    titleFeature: String,
    descriptionFeature: String
) {
    GenericCard(
        containerColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary
            )

            Column {
                Text(
                    text = titleFeature,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = CustomFontSize.Sixteen,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = descriptionFeature,
                    fontSize = CustomFontSize.Twelve,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .85f)
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
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = titleFeature, color = MaterialTheme.colorScheme.onPrimary)
            Text(text = descriptionFeature, color = MaterialTheme.colorScheme.onPrimary)
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
            ) {
                Text(text = buttonText, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
