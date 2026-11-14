package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth8
import com.msoula.hobbymatchmaker.core.design.cancel
import com.msoula.hobbymatchmaker.core.design.edit_profile_interests_add_confirm_cd
import com.msoula.hobbymatchmaker.core.design.edit_profile_interests_add_own
import com.msoula.hobbymatchmaker.core.design.edit_profile_interests_custom_placeholder
import com.msoula.hobbymatchmaker.core.design.edit_profile_interests_hint
import com.msoula.hobbymatchmaker.core.design.edit_profile_interests_limit
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapCheck
import com.msoula.hobbymatchmaker.core.design.icons.LucideInfo
import com.msoula.hobbymatchmaker.core.design.icons.MaterialIconsClose
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsAdd_2
import com.msoula.hobbymatchmaker.core.design.interest_suggestion_action
import com.msoula.hobbymatchmaker.core.design.interest_suggestion_animation
import com.msoula.hobbymatchmaker.core.design.interest_suggestion_comedy
import com.msoula.hobbymatchmaker.core.design.interest_suggestion_documentary
import com.msoula.hobbymatchmaker.core.design.interest_suggestion_drama
import com.msoula.hobbymatchmaker.core.design.interest_suggestion_fantasy
import com.msoula.hobbymatchmaker.core.design.interest_suggestion_horror
import com.msoula.hobbymatchmaker.core.design.interest_suggestion_music
import com.msoula.hobbymatchmaker.core.design.interest_suggestion_romance
import com.msoula.hobbymatchmaker.core.design.interest_suggestion_sci_fi
import com.msoula.hobbymatchmaker.core.design.interest_suggestion_thriller
import com.msoula.hobbymatchmaker.core.design.interest_suggestion_travel
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.HMMTextFieldColors
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import com.msoula.hobbymatchmaker.core.design.user_profile_interests_title
import com.msoula.hobbymatchmaker.core.design.util.dashedBorder
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

private const val MAX_INTERESTS = 5
private const val CUSTOM_INTEREST_MAX_LENGTH = 20

/**
 * Interests picker: tap suggested chips to toggle them, or add a single custom tag.
 * Interests stay plain strings; suggestions and custom tags are matched ignoring case.
 */
@Composable
fun ProfileEditInterestsForm(
    modifier: Modifier = Modifier,
    interests: ImmutableList<String>,
    onInterestChanged: (List<String>) -> Unit
) {
    val suggestions = rememberSuggestedInterests()
    var isAddingCustom by remember { mutableStateOf(false) }
    var customInput by remember { mutableStateOf("") }
    val isFull = interests.size >= MAX_INTERESTS

    // Custom tags (not part of the suggestions) come first so they never get lost
    val customTags = interests.filter { tag -> suggestions.none { it.equals(tag, ignoreCase = true) } }
    val options = customTags + suggestions

    fun commitCustomInterest() {
        val tag = customInput.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val alreadySelected = interests.any { it.equals(tag, ignoreCase = true) }
        if (tag.any { it.isLetter() } && !alreadySelected && !isFull) {
            // A custom tag that matches a suggestion reuses the suggestion's spelling
            onInterestChanged(interests + (suggestions.firstOrNull { it.equals(tag, ignoreCase = true) } ?: tag))
        }
        customInput = ""
        isAddingCustom = false
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.TwentyFour),
        verticalArrangement = Arrangement.spacedBy(CustomSize.Eight)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EditSectionLabel(text = stringResource(Res.string.user_profile_interests_title))
            SpacerWidth8()
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = .15f)
            ) {
                Text(
                    text = "${interests.size}/$MAX_INTERESTS",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = CustomSize.Eight, vertical = CustomSize.Four)
                )
            }
        }

        Text(
            text = stringResource(Res.string.edit_profile_interests_hint),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f),
            style = MaterialTheme.typography.bodySmall
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(CustomSize.Eight),
            verticalArrangement = Arrangement.spacedBy(CustomSize.Eight)
        ) {
            options.forEach { option ->
                val selected = interests.any { it.equals(option, ignoreCase = true) }
                InterestOptionChip(
                    label = option,
                    selected = selected,
                    enabled = selected || !isFull,
                    onClick = {
                        if (selected) {
                            onInterestChanged(interests.filterNot { it.equals(option, ignoreCase = true) })
                        } else {
                            onInterestChanged(interests + option)
                        }
                    }
                )
            }
            if (!isAddingCustom) {
                AddCustomInterestChip(enabled = !isFull, onClick = { isAddingCustom = true })
            }
        }

        if (isAddingCustom && !isFull) {
            CustomInterestInput(
                value = customInput,
                onValueChange = { if (it.length <= CUSTOM_INTEREST_MAX_LENGTH) customInput = it },
                onConfirm = ::commitCustomInterest,
                onCancel = {
                    customInput = ""
                    isAddingCustom = false
                }
            )
        }

        if (isFull) {
            InterestLimitBanner()
        }
    }
}

@Composable
internal fun rememberSuggestedInterests(): List<String> =
    listOf(
        stringResource(Res.string.interest_suggestion_sci_fi),
        stringResource(Res.string.interest_suggestion_thriller),
        stringResource(Res.string.interest_suggestion_drama),
        stringResource(Res.string.interest_suggestion_comedy),
        stringResource(Res.string.interest_suggestion_action),
        stringResource(Res.string.interest_suggestion_animation),
        stringResource(Res.string.interest_suggestion_horror),
        stringResource(Res.string.interest_suggestion_romance),
        stringResource(Res.string.interest_suggestion_documentary),
        stringResource(Res.string.interest_suggestion_fantasy),
        stringResource(Res.string.interest_suggestion_music),
        stringResource(Res.string.interest_suggestion_travel)
    )

@Composable
internal fun InterestOptionChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(CustomSize.Sixteen),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = .15f)),
        modifier = Modifier.alpha(if (enabled) 1f else .4f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = CustomSize.Sixteen, vertical = CustomSize.Eight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Icon(
                    imageVector = BootstrapCheck,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(end = CustomSize.Four)
                        .size(IconSize.Sixteen)
                )
            }
            Text(
                text = label,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
internal fun AddCustomInterestChip(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .alpha(if (enabled) 1f else .4f)
            .clip(RoundedCornerShape(CustomSize.Sixteen))
            .clickable(enabled = enabled, onClick = onClick)
            .dashedBorder(MaterialTheme.colorScheme.onSurface.copy(alpha = .4f), CustomSize.Sixteen)
            .padding(horizontal = CustomSize.Sixteen, vertical = CustomSize.Eight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = MaterialSymbolsAdd_2,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(end = CustomSize.Four)
                .size(IconSize.Sixteen)
        )
        Text(
            text = stringResource(Res.string.edit_profile_interests_add_own),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
internal fun CustomInterestInput(
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            placeholder = { Text(text = stringResource(Res.string.edit_profile_interests_custom_placeholder)) },
            singleLine = true,
            colors = HMMTextFieldColors(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onConfirm() })
        )
        IconButton(onClick = onConfirm, enabled = value.any { it.isLetter() }) {
            Icon(
                imageVector = BootstrapCheck,
                contentDescription = stringResource(Res.string.edit_profile_interests_add_confirm_cd),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = if (value.any { it.isLetter() }) 1f else .4f)
            )
        }
        IconButton(onClick = onCancel) {
            Icon(
                imageVector = MaterialIconsClose,
                contentDescription = stringResource(Res.string.cancel),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f)
            )
        }
    }
}

@Composable
internal fun InterestLimitBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CustomSize.Sixteen),
        color = MaterialTheme.colorScheme.primary.copy(alpha = .1f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = .3f))
    ) {
        Row(
            modifier = Modifier.padding(CustomSize.Sixteen),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = LucideInfo,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(IconSize.Sixteen)
            )
            SpacerWidth8()
            Text(
                text = stringResource(Res.string.edit_profile_interests_limit),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
