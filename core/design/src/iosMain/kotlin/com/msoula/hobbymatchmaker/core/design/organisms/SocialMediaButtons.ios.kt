package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.apple_alt
import com.msoula.hobbymatchmaker.core.design.apple_logo
import com.msoula.hobbymatchmaker.core.design.atoms.SocialMediaButton
import com.msoula.hobbymatchmaker.core.design.google_alt
import com.msoula.hobbymatchmaker.core.design.google_logo
import com.msoula.hobbymatchmaker.core.design.sign_in_with_apple
import com.msoula.hobbymatchmaker.core.design.sign_in_with_google
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.painterResource
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
            text = stringResource(Res.string.sign_in_with_apple),
            contentDescription = stringResource(Res.string.apple_alt),
            painter = painterResource(Res.drawable.apple_logo),
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = Color.Black,
            borderStroke = BorderStroke(1.dp, Color.Black),
            onClick = onAppleClick,
            loading = loading
        )

        SocialMediaButton(
            text = stringResource(Res.string.sign_in_with_google),
            contentDescription = stringResource(Res.string.google_alt),
            painter = painterResource(Res.drawable.google_logo),
            containerColor = Color.Black,
            contentColor = MaterialTheme.colorScheme.onSurface,
            borderStroke = null,
            onClick = onGoogleClick,
            loading = loading
        )
    }
}
