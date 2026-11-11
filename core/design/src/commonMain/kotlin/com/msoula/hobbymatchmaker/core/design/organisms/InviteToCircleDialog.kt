package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryAlertDialog
import com.msoula.hobbymatchmaker.core.design.atoms.UserAvatar
import com.msoula.hobbymatchmaker.core.design.cancel
import com.msoula.hobbymatchmaker.core.design.models.InviteCandidate
import com.msoula.hobbymatchmaker.core.design.models.InviteSearchStatus
import com.msoula.hobbymatchmaker.core.design.social_invite_dialog_no_results
import com.msoula.hobbymatchmaker.core.design.social_invite_dialog_search_hint
import com.msoula.hobbymatchmaker.core.design.social_invite_dialog_send
import com.msoula.hobbymatchmaker.core.design.social_invite_dialog_title
import com.msoula.hobbymatchmaker.core.design.social_search_already_invited
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

// The caller clears the search results when the dialog goes away
@Composable
fun InviteToCircleDialog(
    candidates: ImmutableList<InviteCandidate>,
    searchStatus: InviteSearchStatus,
    onSearch: (String) -> Unit,
    onDismiss: () -> Unit,
    onSendInvite: (pseudo: String, name: String?) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedPseudo by rememberSaveable { mutableStateOf<String?>(null) }

    PrimaryAlertDialog(
        paddingValues = PaddingValues(),
        title = stringResource(Res.string.social_invite_dialog_title),
        confirmButtonText = stringResource(Res.string.social_invite_dialog_send),
        cancelButtonText = stringResource(Res.string.cancel),
        isEnabled = selectedPseudo != null,
        isLoading = false,
        onCancel = onDismiss,
        onDismiss = onDismiss,
        onConfirm = {
            candidates.firstOrNull { it.pseudo == selectedPseudo }?.let { candidate ->
                onSendInvite(candidate.pseudo, candidate.name.ifBlank { null })
            }
        }
    ) {
        Column {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    selectedPseudo = null
                    onSearch(it)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(CustomSize.Sixteen),
                placeholder = { Text(stringResource(Res.string.social_invite_dialog_search_hint)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false
                )
            )

            when {
                candidates.isNotEmpty() -> CandidateList(
                    candidates = candidates,
                    selectedPseudo = selectedPseudo,
                    onCandidateSelected = { selectedPseudo = it }
                )

                searchStatus == InviteSearchStatus.Searching -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = CustomSize.Sixteen),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                searchStatus == InviteSearchStatus.Done -> Text(
                    text = stringResource(Res.string.social_invite_dialog_no_results),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = CustomSize.Sixteen)
                )
            }
        }
    }
}

@Composable
internal fun CandidateList(
    candidates: ImmutableList<InviteCandidate>,
    selectedPseudo: String?,
    onCandidateSelected: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(top = CustomSize.Eight)
            .heightIn(max = 240.dp),
        verticalArrangement = Arrangement.spacedBy(CustomSize.Four)
    ) {
        items(candidates, key = { it.pseudo }) { candidate ->
            val isSelected = candidate.pseudo == selectedPseudo

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = .12f) else MaterialTheme.colorScheme.surface,
                        RoundedCornerShape((CustomSize.Eight + CustomSize.Four))
                    )
                    .selectable(
                        selected = isSelected,
                        enabled = !candidate.isInvited,
                        onClick = { onCandidateSelected(candidate.pseudo) },
                        role = Role.RadioButton
                    )
                    .padding(CustomSize.Eight),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy((CustomSize.Eight + CustomSize.Four))
            ) {
                UserAvatar(
                    avatarUrl = candidate.avatarUrl,
                    contentDescription = candidate.name.ifBlank { candidate.pseudo },
                    size = CustomSize.FortyEight - CustomSize.Eight
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = candidate.name.ifBlank { "@${candidate.pseudo}" },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (candidate.name.isNotBlank()) {
                        Text(
                            text = "@${candidate.pseudo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (candidate.isInvited) {
                    Text(
                        text = stringResource(Res.string.social_search_already_invited),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    RadioButton(selected = isSelected, onClick = null)
                }
            }
        }
    }
}
