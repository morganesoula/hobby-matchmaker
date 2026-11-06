package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList

private const val NO_REASON_SELECTED = -1

// The caller maps the selected index back to its own reason type
@Composable
fun ReportReasonDialog(
    title: String,
    message: String,
    reasons: ImmutableList<String>,
    confirmButtonText: String,
    cancelButtonText: String,
    onDismiss: () -> Unit,
    onConfirm: (selectedIndex: Int) -> Unit
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(NO_REASON_SELECTED) }

    PrimaryAlertDialog(
        paddingValues = PaddingValues(),
        title = title,
        confirmButtonText = confirmButtonText,
        cancelButtonText = cancelButtonText,
        isEnabled = selectedIndex != NO_REASON_SELECTED,
        isLoading = false,
        onCancel = onDismiss,
        onDismiss = onDismiss,
        onConfirm = { onConfirm(selectedIndex) }
    ) {
        Column {
            Text(text = message, style = MaterialTheme.typography.bodyMedium)

            reasons.forEachIndexed { index, reason ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = index == selectedIndex,
                            onClick = { selectedIndex = index },
                            role = Role.RadioButton
                        )
                        .padding(vertical = CustomSize.Four),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = index == selectedIndex, onClick = null)
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = CustomSize.Eight)
                    )
                }
            }
        }
    }
}
