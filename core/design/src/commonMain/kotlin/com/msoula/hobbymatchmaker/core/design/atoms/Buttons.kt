package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.theme.CustomFontSize
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.disabledContainerColor
import com.msoula.hobbymatchmaker.core.design.theme.onDisabledColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    loading: Boolean,
    enabled: Boolean
) {
    Button(
        onClick = { onClick() },
        enabled = enabled && !loading,
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = disabledContainerColor(),
            disabledContentColor = onDisabledColor()
        ),
        shape = RoundedCornerShape(CustomSize.Eight),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = CustomSize.TwentyFour),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(CustomSize.TwentyFour),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = CustomSize.Two
            )
        } else {
            Text(
                text = text,
                modifier = modifier.padding(CustomSize.Eight),
                fontSize = CustomFontSize.Sixteen,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.primary.copy(alpha = .6f),
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = .6f)),
        shape = RoundedCornerShape(CustomSize.Eight),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = CustomSize.TwentyFour),
    ) {
        Text(
            text = text,
            modifier = modifier.padding(CustomSize.Eight),
            fontSize = CustomFontSize.Sixteen,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun SocialMediaButton(
    modifier: Modifier = Modifier,
    text: String,
    contentDescription: String,
    iconRes: DrawableResource,
    containerColor: Color,
    contentColor: Color,
    borderStroke: BorderStroke?,
    onClick: () -> Unit,
    loading: Boolean
) {
    OutlinedButton(
        onClick = { onClick() },
        shape = RoundedCornerShape(CustomSize.Eight),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.TwentyFour, vertical = CustomSize.Four),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = borderStroke
    ) {
        if (loading) {
            CircularProgressIndicator()
        } else {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = contentDescription,
                tint = Color.Unspecified,
                modifier = Modifier.size(CustomSize.TwentyFour)
            )
            Spacer(modifier = Modifier.width(CustomSize.Eight))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
fun SecondButtonPreview() {
    SecondaryButton(text = "Sign up", onClick = {})
}
