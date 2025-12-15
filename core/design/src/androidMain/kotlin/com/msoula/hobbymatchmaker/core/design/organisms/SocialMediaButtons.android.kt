package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.SocialMediaButton
import com.msoula.hobbymatchmaker.core.design.facebook_alt
import com.msoula.hobbymatchmaker.core.design.facebook_logo
import com.msoula.hobbymatchmaker.core.design.google_alt
import com.msoula.hobbymatchmaker.core.design.google_logo
import com.msoula.hobbymatchmaker.core.design.sign_in_with_facebook
import com.msoula.hobbymatchmaker.core.design.sign_in_with_google
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun SocialMediaButtonsPlatformSpecific(
    modifier: Modifier,
    onGoogleClick: () -> Unit,
    onAppleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    loading: Boolean
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(CustomSize.Eight)
    ) {
        SocialMediaButton(
            text = stringResource(Res.string.sign_in_with_google),
            contentDescription = stringResource(Res.string.google_alt),
            iconRes = Res.drawable.google_logo,
            containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else Color.White,
            contentColor = if (isSystemInDarkTheme()) Color.Black else MaterialTheme.colorScheme.onSurface,
            borderStroke = BorderStroke(1.dp, Color.LightGray),
            onClick = onGoogleClick,
            loading = loading
        )

        SocialMediaButton(
            text = stringResource(Res.string.sign_in_with_facebook),
            contentDescription = stringResource(Res.string.facebook_alt),
            iconRes = Res.drawable.facebook_logo,
            containerColor = Color(0xFF1877F2),
            contentColor = MaterialTheme.colorScheme.onSurface,
            borderStroke = null,
            onClick = onFacebookClick,
            loading = loading
        )
    }
}
