package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.already_a_member
import com.msoula.hobbymatchmaker.core.design.already_a_member_connect
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_button_title
import com.msoula.hobbymatchmaker.core.design.new_member
import com.msoula.hobbymatchmaker.core.design.new_member_clickable_part
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource

@Composable
fun AuthenticationScreenBottom(
    modifier: Modifier = Modifier,
    isSignInScreen: Boolean,
    onNavigateToOppositeScreen: () -> Unit,
    onContinueAsGuest: () -> Unit = {},
    guestButtonEnabled: Boolean = true
) {
    val linkColor = if (isSystemInDarkTheme()) Color(0, 191, 255) else Color.Blue

    val annotatedString = buildAnnotatedString {
        if (isSignInScreen) {
            append(stringResource(Res.string.new_member) + " ")
            pushStringAnnotation(tag = "clickable", annotation = "link")
            withStyle(
                style = SpanStyle(
                    color = linkColor,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(stringResource(Res.string.new_member_clickable_part))
            }
            pop()
        } else {
            append(stringResource(Res.string.already_a_member) + " ")
            pushStringAnnotation(tag = "clickable", annotation = "link")
            withStyle(
                style = SpanStyle(
                    color = linkColor,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(stringResource(Res.string.already_a_member_connect))
            }
            pop()
        }
    }

    if (isSignInScreen) {
        OutlinedButton(
            onClick = { onContinueAsGuest() },
            enabled = guestButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = CustomSize.Sixteen,
                    end = CustomSize.Sixteen
                ),
            shape = RoundedCornerShape(CustomSize.Eight),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = stringResource(Res.string.continue_as_guest_button_title),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }

    Text(
        text = annotatedString,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                bottom = CustomSize.Sixteen,
                top = if (isSignInScreen) CustomSize.Eight else 0.dp
            )
            .semantics {
                role = Role.Button
                contentDescription = annotatedString.text
            }
            .clickable {
                annotatedString
                    .getStringAnnotations("clickable", 0, annotatedString.length)
                    .firstOrNull()
                    ?.let { if (it.item == "link") onNavigateToOppositeScreen() }
            },
        style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
    )
}
