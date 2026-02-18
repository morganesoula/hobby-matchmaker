package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
import com.msoula.hobbymatchmaker.core.design.molecules.MemberItem
import com.msoula.hobbymatchmaker.core.design.movie_detail_friend_section_main_title
import com.msoula.hobbymatchmaker.core.design.movie_detail_friend_section_no_data_title
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendSection(
    friends: ImmutableList<ProfileSocialMember>
) {
    Column {
        Text(
            text = stringResource(Res.string.movie_detail_friend_section_main_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        SpacerHeight8()
        Row {
            if (friends.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(CustomSize.Eight)) {
                    items(friends, key = { it.uid }) { friend ->
                        MemberItem(friend, displayMoviesCount = false)
                    }
                }
            } else {
                Text(
                    text = stringResource(Res.string.movie_detail_friend_section_no_data_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
