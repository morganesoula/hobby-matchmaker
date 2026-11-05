package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.UserAvatar
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsAdd_2
import com.msoula.hobbymatchmaker.core.design.social_circle_empty_slot_description
import com.msoula.hobbymatchmaker.core.design.social_circle_empty_slot_title
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource

@Composable
fun CircleMemberCard(
    modifier: Modifier = Modifier,
    memberName: String,
    memberPseudo: String,
    memberAvatarUrl: String,
    commonMoviesCount: Int = 0
) {
    GenericCard(
        modifier = modifier.padding(horizontal = CustomSize.Sixteen),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
        ) {
            UserAvatar(avatarUrl = memberAvatarUrl, contentDescription = memberName)

            Column(modifier = Modifier.weight(1f)) {
                InvitationIdentity(guestName = memberName, guestPseudo = memberPseudo)
                CommonFavoritesLabel(commonMoviesCount)
            }
        }
    }
}

@Composable
fun EmptyCircleSlotCard(modifier: Modifier = Modifier) {
    val dashColor = MaterialTheme.colorScheme.primary.copy(alpha = .4f)
    val cornerRadius = CustomSize.Sixteen

    Row(
        modifier = modifier
            .padding(horizontal = CustomSize.Sixteen)
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = dashColor,
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(cornerRadius.toPx()),
                    style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f)))
                )
            }
            .padding(CustomSize.Sixteen),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
    ) {
        Box(
            modifier = Modifier
                .size(CustomSize.FortyEight)
                .clip(CircleShape)
                .border(1.dp, dashColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = MaterialSymbolsAdd_2,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(CustomSize.TwentyFour)
            )
        }

        Column {
            Text(
                text = stringResource(Res.string.social_circle_empty_slot_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(Res.string.social_circle_empty_slot_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
