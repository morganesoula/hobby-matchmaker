package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.msoula.hobbymatchmaker.core.design.atoms.SmallBodyText
import com.msoula.hobbymatchmaker.core.design.theme.CustomFontSize

/**
 * Text link with the contact address shown underneath, so users can still reach us
 * when no app can handle [onClick] (e.g. no email app installed).
 */
@Composable
fun ContactLink(
    modifier: Modifier = Modifier,
    text: String,
    contactInfo: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = { onClick() },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = text,
                fontSize = CustomFontSize.Sixteen,
                textAlign = TextAlign.Center
            )
        }
        SmallBodyText(text = contactInfo)
    }
}
