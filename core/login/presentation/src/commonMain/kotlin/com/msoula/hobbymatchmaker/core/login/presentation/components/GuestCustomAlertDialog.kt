package com.msoula.hobbymatchmaker.core.login.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
        title = { Text("") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Vous pouvez utiliser l'app sans compte blablabla")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = dontAskAgain, onCheckedChange = { dontAskAgain = it })
                    Text("Ne plus me demander")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
                onContinue(dontAskAgain)
            }) {
                Text("Continuer quand même")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                onCreateAccount()
            }) {
                Text("Créer un compte")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        ),
        modifier = Modifier.widthIn(420.dp)
    )
}
