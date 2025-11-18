package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.atoms.FormIcon
import com.msoula.hobbymatchmaker.core.design.icons.Delete
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun InterestsBlock(
    modifier: Modifier = Modifier,
    interests: List<String>?
) {
    FlowRow {
        interests?.forEach { tag ->
            AssistChip(
                modifier = modifier.padding(CustomSize.Four),
                onClick = {},
                label = { Text(tag) },
                enabled = false,
                colors = AssistChipDefaults.assistChipColors(
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .4f),
                    disabledLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun InterestDeletableBlock(
    modifier: Modifier = Modifier,
    interests: List<String>?,
    onDeleteClicked: (tag: String) -> Unit
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(CustomSize.Four),
        horizontalArrangement = Arrangement.spacedBy(CustomSize.Eight)
    ) {
        interests?.forEach { tag ->
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .border(
                        1.dp, MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(CustomSize.Eight)
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(CustomSize.Four)
            ) {
                Text(
                    text = tag,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        start = CustomSize.Eight
                    )
                )

                IconButton(
                    onClick = { onDeleteClicked(tag) },
                    modifier = Modifier.wrapContentSize()
                ) {
                    FormIcon(
                        icon = Delete,
                        size = IconSize.TwentyFour,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun InterestDeletableBlockPreview() {
    InterestDeletableBlock(
        interests = listOf("Chat", "Chien", "Lapin", "Renard", "Randonnée"),
        onDeleteClicked = {}
    )
}
