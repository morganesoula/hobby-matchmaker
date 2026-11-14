package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth8
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsAdd_2
import com.msoula.hobbymatchmaker.core.design.molecules.InterestsBlock
import com.msoula.hobbymatchmaker.core.design.profile_interests_add
import com.msoula.hobbymatchmaker.core.design.profile_interests_empty_button
import com.msoula.hobbymatchmaker.core.design.profile_interests_empty_text
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import com.msoula.hobbymatchmaker.core.design.user_profile_interests_title
import com.msoula.hobbymatchmaker.core.design.util.dashedBorder
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileInterestsSection(
    interests: ImmutableList<String>,
    onAddClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.TwentyFour),
        verticalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InterestsTitle(count = interests.size)
            Row(
                modifier = Modifier.clickable(onClick = onAddClicked),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.profile_interests_add),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = MaterialSymbolsAdd_2,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(start = CustomSize.Four)
                        .size(IconSize.Sixteen)
                )
            }
        }
        InterestsBlock(interests = interests)
    }
}

@Composable
fun ProfileInterestsEmptySection(
    onAddClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.TwentyFour),
        verticalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
    ) {
        InterestsTitle(count = null)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CustomSize.Sixteen),
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = .1f))
        ) {
            Column(
                modifier = Modifier.padding(CustomSize.Sixteen),
                verticalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
            ) {
                Text(
                    text = stringResource(Res.string.profile_interests_empty_text),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .8f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(CustomSize.Sixteen))
                        .clickable(onClick = onAddClicked)
                        .dashedBorder(MaterialTheme.colorScheme.primary.copy(alpha = .6f), CustomSize.Sixteen)
                        .padding(horizontal = CustomSize.Sixteen, vertical = CustomSize.Eight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = MaterialSymbolsAdd_2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(IconSize.Sixteen)
                    )
                    SpacerWidth8()
                    Text(
                        text = stringResource(Res.string.profile_interests_empty_button),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
internal fun InterestsTitle(count: Int?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(Res.string.user_profile_interests_title),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        count?.let {
            Text(
                text = " ($it)",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
