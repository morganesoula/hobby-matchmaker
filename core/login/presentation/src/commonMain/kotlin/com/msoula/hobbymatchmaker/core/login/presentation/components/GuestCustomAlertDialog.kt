package com.msoula.hobbymatchmaker.core.login.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_create_redirect_button
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_dialog_text
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_dialog_title
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_dont_ask_again
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_validation_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun GuestModeDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onContinue: (dontAskAgain: Boolean) -> Unit,
    onCreateAccount: () -> Unit
) {
    if (!show) return
    var dontAskAgain by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.continue_as_guest_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(Res.string.continue_as_guest_dialog_text))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = dontAskAgain, onCheckedChange = { dontAskAgain = it })
                    Text(stringResource(Res.string.continue_as_guest_dont_ask_again))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
                onContinue(dontAskAgain)
            }) {
                Text(stringResource(Res.string.continue_as_guest_validation_button))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                onCreateAccount()
            }) {
                Text(
                    stringResource(
                        Res.string.continue_as_guest_create_redirect_button
                    )
                )
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        ),
        modifier = Modifier.widthIn(400.dp)
    )
}
