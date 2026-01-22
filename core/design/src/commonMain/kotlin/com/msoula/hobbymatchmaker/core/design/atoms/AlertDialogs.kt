package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.msoula.hobbymatchmaker.core.design.theme.CustomFontSize
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

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

@Composable
fun MinimalDialog(
    noDataText: String,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(CustomSize.Sixteen),
            shape = RoundedCornerShape(CustomSize.Sixteen)
        ) {
            Text(
                text = noDataText,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }
}
