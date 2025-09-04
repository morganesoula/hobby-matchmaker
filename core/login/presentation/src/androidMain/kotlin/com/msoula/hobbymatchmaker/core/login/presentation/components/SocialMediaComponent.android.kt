package com.msoula.hobbymatchmaker.core.login.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.facebook.AccessToken
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.login.presentation.Res
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.facebook_alt
import com.msoula.hobbymatchmaker.core.login.presentation.facebook_logo
import com.msoula.hobbymatchmaker.core.login.presentation.google_alt
import com.msoula.hobbymatchmaker.core.login.presentation.google_logo
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in_with_facebook
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

    Spacer(modifier = Modifier.height(8.dp))

    if (onFacebookButtonClicked != null) {
        OutlinedButton(
            onClick = {
                val token = AccessToken.getCurrentAccessToken()
                if (token != null && !token.isExpired) {
                    return@OutlinedButton
                }

                facebookUIClient?.let { fbClient ->
                    fbClient.registerCallback(
                        onSuccess = { credential, _ ->
                            onFacebookButtonClicked(credential)
                        },
                        onError = {
                            Logger.d("Error fetching Facebook credentials")
                        }
                    )

                    facebookUIClient.logIn()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1877F2),
                contentColor = Color.White
            )
        ) {
            Icon(
                painter = painterResource(Res.drawable.facebook_logo),
                contentDescription = stringResource(Res.string.facebook_alt),
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.sign_in_with_facebook),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
