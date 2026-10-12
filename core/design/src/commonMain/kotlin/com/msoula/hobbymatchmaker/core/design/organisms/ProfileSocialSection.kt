package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth8
import com.msoula.hobbymatchmaker.core.design.authentified_no_social_members_description
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import com.msoula.hobbymatchmaker.core.design.user_profile_social_circle_common_movies_count_plural
import com.msoula.hobbymatchmaker.core.design.user_profile_social_circle_common_movies_count_singular
import com.msoula.hobbymatchmaker.core.design.user_profile_social_circle_main_title
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileSocialSection(
    socialMembers: ImmutableList<ProfileSocialMember>?
) {
    GenericCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.TwentyFour),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(Modifier.padding(CustomSize.Eight)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.user_profile_social_circle_main_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            SpacerHeight16()

            if (!socialMembers.isNullOrEmpty()) {
                socialMembers.forEachIndexed { index, member ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = CustomSize.Eight),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SubcomposeAsyncImage(
                            model = member.avatarUrl,
                            contentDescription = member.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(IconSize.FortyEight)
                                .clip(CircleShape)
                                .border(
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onBackground
                                    ),
                                    shape = CircleShape
                                )
                        )

                        SpacerWidth8()

                        Column {
                            Text(
                                text = member.name ?: member.pseudo,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            member.commonMoviesCount?.let { count ->
                                if (count > 0) {
                                    Text(
                                        text = when (count) {
                                            1 -> stringResource(Res.string.user_profile_social_circle_common_movies_count_singular)
                                            else -> "$count " + stringResource(Res.string.user_profile_social_circle_common_movies_count_plural)
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    if (index < socialMembers.lastIndex) {
                        HorizontalDivider(
                            Modifier.padding(vertical = CustomSize.Four),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = .2f)
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(Res.string.authentified_no_social_members_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
