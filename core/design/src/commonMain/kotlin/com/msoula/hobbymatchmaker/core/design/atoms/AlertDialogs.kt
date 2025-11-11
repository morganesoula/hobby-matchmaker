package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import com.msoula.hobbymatchmaker.core.design.theme.CustomFontSize

@Composable
fun PrimaryAlertDialog(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    title: String,
    confirmButtonText: String,
    cancelButtonText: String,
    isEnabled: Boolean,
    isLoading: Boolean,
    onCancel: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    AlertDialog(
        modifier = modifier.padding(paddingValues),
        title = {
            Text(
                text = title,
                fontSize = CustomFontSize.Sixteen,
                textAlign = TextAlign.Center
            )
        },
        text = { content() },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        keyboardController?.hide()
                        onConfirm()
                    },
                    enabled = isEnabled
                ) {
                    Text(text = confirmButtonText)
                }
            }
        },
        dismissButton = {
            Button(
                onClick = { onCancel() },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
            ) {
                Text(text = cancelButtonText)
            }
        }
    )
}
