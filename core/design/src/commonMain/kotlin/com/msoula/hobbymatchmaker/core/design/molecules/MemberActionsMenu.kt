package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsMoreVert
import com.msoula.hobbymatchmaker.core.design.social_member_menu_block
import com.msoula.hobbymatchmaker.core.design.social_member_menu_description
import com.msoula.hobbymatchmaker.core.design.social_member_menu_remove
import com.msoula.hobbymatchmaker.core.design.social_member_menu_report
import org.jetbrains.compose.resources.stringResource

// Remove only makes sense for people already in the circle, so it is optional
@Composable
fun MemberActionsMenu(
    modifier: Modifier = Modifier,
    onRemoveClick: (() -> Unit)? = null,
    onReportClick: () -> Unit,
    onBlockClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = MaterialSymbolsMoreVert,
                contentDescription = stringResource(Res.string.social_member_menu_description),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            onRemoveClick?.let { onRemove ->
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.social_member_menu_remove)) },
                    onClick = {
                        expanded = false
                        onRemove()
                    }
                )
            }
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.social_member_menu_report)) },
                onClick = {
                    expanded = false
                    onReportClick()
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(Res.string.social_member_menu_block),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    expanded = false
                    onBlockClick()
                }
            )
        }
    }
}
