package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.icons.FontAwesomeUserFriends
import com.msoula.hobbymatchmaker.core.design.icons.LucideFilm
import com.msoula.hobbymatchmaker.core.design.movies_liked_count_title_plural
import com.msoula.hobbymatchmaker.core.design.movies_liked_count_title_singular
import com.msoula.hobbymatchmaker.core.design.social_members_count_title_plural
import com.msoula.hobbymatchmaker.core.design.social_members_count_title_singular
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileStatsSection(
    modifier: Modifier = Modifier,
    moviesLikedCount: Int,
    socialMembersCount: Int
) {
    GenericCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.TwentyFour),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = CustomSize.Sixteen),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = LucideFilm,
                value = moviesLikedCount.toString(),
                label = if (moviesLikedCount < 2)
                    stringResource(Res.string.movies_liked_count_title_singular)
                else
                    stringResource(Res.string.movies_liked_count_title_plural)
            )

            StatItem(
                icon = FontAwesomeUserFriends,
                value = "$socialMembersCount/5",
                label = if (socialMembersCount < 2)
                    stringResource(Res.string.social_members_count_title_singular)
                else
                    stringResource(Res.string.social_members_count_title_plural)
            )
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    val color = MaterialTheme.colorScheme.primary

    Row(
        Modifier
            .background(color.copy(alpha = .15f), RoundedCornerShape(CustomSize.Eight))
            .padding(all = CustomSize.Sixteen),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .padding(CustomSize.Eight)
                .size(CustomSize.ThirtyTwo)
        )
        Spacer(Modifier.width(CustomSize.Eight))
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                value,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
