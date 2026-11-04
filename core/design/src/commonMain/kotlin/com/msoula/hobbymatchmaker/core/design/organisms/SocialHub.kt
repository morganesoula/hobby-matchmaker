package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth4
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapArrowRight
import com.msoula.hobbymatchmaker.core.design.icons.MaterialIconsArrow_back
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsPerson_add
import com.msoula.hobbymatchmaker.core.design.models.SocialTab
import com.msoula.hobbymatchmaker.core.design.social_active_circle_full
import com.msoula.hobbymatchmaker.core.design.social_active_circle_manage
import com.msoula.hobbymatchmaker.core.design.social_active_circle_slot_available
import com.msoula.hobbymatchmaker.core.design.social_active_circle_slots_available
import com.msoula.hobbymatchmaker.core.design.social_active_circle_title
import com.msoula.hobbymatchmaker.core.design.social_hub_title
import com.msoula.hobbymatchmaker.core.design.social_invite_button
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun SocialHubHeader(
    modifier: Modifier = Modifier,
    showBackArrow: Boolean,
    onBack: () -> Unit,
    onInviteClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.Sixteen, vertical = CustomSize.Eight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBackArrow) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = MaterialIconsArrow_back,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = stringResource(Res.string.social_hub_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .weight(1f)
                .padding(start = if (showBackArrow) CustomSize.Four else CustomSize.Eight)
        )

        Button(
            onClick = onInviteClick,
            shape = RoundedCornerShape(CustomSize.Sixteen + CustomSize.Eight)
        ) {
            Icon(
                imageVector = MaterialSymbolsPerson_add,
                contentDescription = null,
                modifier = Modifier.size(CustomSize.Sixteen + CustomSize.Two)
            )
            SpacerWidth4()
            Text(
                text = stringResource(Res.string.social_invite_button),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ActiveCircleCard(
    modifier: Modifier = Modifier,
    filledSlots: Int,
    maxMembers: Int,
    onManageClick: () -> Unit
) {
    val slotsLeft = (maxMembers - filledSlots).coerceAtLeast(0)

    GenericCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.Sixteen),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.social_active_circle_title).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(
                    modifier = Modifier.clickable(onClick = onManageClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.social_active_circle_manage),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    SpacerWidth4()
                    Icon(
                        imageVector = BootstrapArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(CustomSize.Sixteen)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { if (maxMembers > 0) filledSlots.toFloat() / maxMembers else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = CustomSize.Sixteen)
                    .height(CustomSize.Eight)
                    .clip(CircleShape),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(
                text = when (slotsLeft) {
                    0 -> stringResource(Res.string.social_active_circle_full)
                    1 -> stringResource(Res.string.social_active_circle_slot_available)
                    else -> stringResource(Res.string.social_active_circle_slots_available, slotsLeft)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = CustomSize.Four),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun SocialTabRow(
    modifier: Modifier = Modifier,
    tabs: ImmutableList<SocialTab>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.Sixteen, vertical = CustomSize.Sixteen)
            .clip(RoundedCornerShape(CustomSize.Sixteen))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(CustomSize.Four)
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = index == selectedIndex
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(CustomSize.Sixteen - CustomSize.Four))
                    .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = CustomSize.Eight),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                SpacerWidth4()
                Text(
                    text = tab.countLabel,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .clip(RoundedCornerShape(CustomSize.Eight))
                        .background(
                            if (selected) MaterialTheme.colorScheme.onPrimary.copy(alpha = .2f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .padding(horizontal = CustomSize.Eight - CustomSize.Two, vertical = CustomSize.Two),
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
