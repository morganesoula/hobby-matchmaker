package com.msoula.hobbymatchmaker.core.login.presentation.sign_in.utils

import android.app.Activity
import android.content.IntentSender
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.models.SignInResultModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class GetSignInIntentResult(
    private val launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
) {
    fun launch(signInIntentSender: IntentSender?) {
        launcher.launch(
            IntentSenderRequest.Builder(
                signInIntentSender ?: return,
            ).build(),
        )
    }
}

@Composable
fun rememberGetSignInIntentResult(
    googleAuthUiClient: GoogleAuthUIClient,
    onGoogleSignInEvent: (SignInResultModel) -> Unit,
    coroutineScope: CoroutineScope,
    setGoogleButtonValueClicked: () -> Unit
): GetSignInIntentResult {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                coroutineScope.launch {
                    val signInResult =
                        googleAuthUiClient.signInWithIntent(
                            intent = result.data ?: return@launch,
                        )

                    onGoogleSignInEvent(signInResult)
                }
            } else {
                Log.d("HMM", "Cancelling signIn")
                setGoogleButtonValueClicked()
            }
        }

    return remember(launcher) {
        GetSignInIntentResult(launcher)
    }
}
