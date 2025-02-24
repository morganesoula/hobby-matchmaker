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
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.login.presentation.Res
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.facebook_alt
import com.msoula.hobbymatchmaker.core.login.presentation.facebook_logo
import com.msoula.hobbymatchmaker.core.login.presentation.google_alt
import com.msoula.hobbymatchmaker.core.login.presentation.google_logo
import dev.gitlive.firebase.auth.AuthCredential
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SocialMediaRowCustom(
    modifier: Modifier = Modifier,
    onFacebookButtonClicked: () -> Unit,
    googleClient: GoogleUIClient,
    onGoogleButtonClicked: (googleClient: GoogleUIClient) -> AuthCredential
) {
    Row(
        modifier =
        modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        OutlinedButton(
            onClick = { onFacebookButtonClicked() },
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = modifier.size(80.dp)
        ) {
            Image(
                modifier = modifier,
                painter = painterResource(Res.drawable.facebook_logo),
                contentDescription = stringResource(Res.string.facebook_alt)
            )
        }

        OutlinedButton(
            onClick = { onGoogleButtonClicked(googleClient) },
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
