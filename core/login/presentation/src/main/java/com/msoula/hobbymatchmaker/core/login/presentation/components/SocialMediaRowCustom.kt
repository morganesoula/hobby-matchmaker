package com.msoula.hobbymatchmaker.core.login.presentation.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.R
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.models.SignInResultModel
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.utils.GoogleAuthUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.utils.rememberGetSignInIntentResult

@Composable
fun SocialMediaRowCustom(
    modifier: Modifier = Modifier,
    onFacebookButtonClicked: () -> Unit,
    googleAuthUIClient: GoogleAuthUIClient,
    onGoogleSignInEvent: (SignInResultModel) -> Unit,
) {
    val googleButtonClicked = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val signInIntent =
        rememberGetSignInIntentResult(
            googleAuthUiClient = googleAuthUIClient,
            onGoogleSignInEvent = onGoogleSignInEvent,
            coroutineScope = coroutineScope,
            setGoogleButtonValueClicked = { googleButtonClicked.value = false },
        )

    LaunchedEffect(googleButtonClicked.value) {
        if (googleButtonClicked.value) {
            Log.d("HMM", "Google Button Clicked is true")
            try {
                val signInIntentSender = googleAuthUIClient.signInWithGoogle()
                Log.d("HMM", "SignInIntentSender is: $signInIntentSender")

                signInIntent.launch(signInIntentSender)
            } catch (exception: Exception) {
                Log.e("HMM", "Error while signInIntent in View: ${exception.message}")
            }

            googleButtonClicked.value = false
        }
    }

    Row(
        modifier =
        modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Button(
            onClick = { onFacebookButtonClicked() },
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)),
            modifier = modifier.size(80.dp),
        ) {
            Image(
                modifier = modifier,
                painter = painterResource(id = R.drawable.facebook_logo),
                contentDescription = stringResource(id = R.string.facebook_alt),
            )
        }

        Button(
            onClick = { googleButtonClicked.value = true },
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)),
            modifier = modifier.size(80.dp),
        ) {
            Image(
                modifier = modifier,
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = stringResource(id = R.string.google_alt),
            )
        }
    }
}
