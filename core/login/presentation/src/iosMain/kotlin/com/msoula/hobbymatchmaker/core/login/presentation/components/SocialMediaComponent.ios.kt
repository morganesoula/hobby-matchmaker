package com.msoula.hobbymatchmaker.core.login.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.login.presentation.Res
import com.msoula.hobbymatchmaker.core.login.presentation.apple_alt
import com.msoula.hobbymatchmaker.core.login.presentation.apple_logo
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.google_alt
import com.msoula.hobbymatchmaker.core.login.presentation.google_logo
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in_with_apple
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in_with_google
import dev.gitlive.firebase.auth.AuthCredential
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun SocialMediaButtonListPlatformSpecificUI(
    modifier: Modifier,
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
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.apple_logo),
                    contentDescription = stringResource(Res.string.apple_alt),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.sign_in_with_apple),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { onGoogleButtonClicked() },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Icon(
                painter = painterResource(Res.drawable.google_logo),
                contentDescription = stringResource(Res.string.google_alt),
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.sign_in_with_google),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
