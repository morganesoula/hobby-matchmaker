package com.msoula.hobbymatchmaker.core.login.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.login.presentation.Res
import com.msoula.hobbymatchmaker.core.login.presentation.apple_alt
import com.msoula.hobbymatchmaker.core.login.presentation.apple_logo
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.google_alt
import com.msoula.hobbymatchmaker.core.login.presentation.google_logo
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import dev.gitlive.firebase.auth.AuthCredential
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun SocialMediaButtonListPlatformSpecificUI(
    modifier: Modifier,
    signInState: SignInEvent,
    onFacebookButtonClicked: ((credential: AuthCredential) -> Unit)?,
    onAppleButtonClicked: (() -> Unit)?,
    onGoogleButtonClicked: () -> Unit,
    facebookUIClient: FacebookUIClient?
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {

        if (onAppleButtonClicked != null) {
            OutlinedButton(
                onClick = { onAppleButtonClicked() },
                enabled = signInState !is SignInEvent.Loading,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = modifier.size(80.dp)
            ) {
                Image(
                    modifier = modifier,
                    painter = painterResource(Res.drawable.apple_logo),
                    contentDescription = stringResource(Res.string.apple_alt),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }

        OutlinedButton(
            onClick = { onGoogleButtonClicked() },
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = modifier.size(80.dp)
        ) {
            Image(
                modifier = modifier,
                painter = painterResource(Res.drawable.google_logo),
                contentDescription = stringResource(Res.string.google_alt)
            )
        }
    }
}
