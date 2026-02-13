package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.CircleWithCustomPhoto
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth4
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_shared_movies_plural_txt
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_shared_movies_single_txt
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
import org.jetbrains.compose.resources.stringResource

@Composable
fun MemberItem(
    member: ProfileSocialMember
) {
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        CircleWithCustomPhoto(customAvatarPath = member.avatarUrl)
        SpacerWidth4()
        Text(
            text = member.name ?: member.pseudo,
            color = MaterialTheme.colorScheme.onBackground
        )
        SpacerWidth4()
        member.commonMoviesCount?.let { count ->
            if (count != 0) {
                Text(
                    text = if (count == 1) {
                        stringResource(
                            Res.string.hub_recent_matches_shared_movies_single_txt
                        )
                    } else {
                        stringResource(
                            Res.string.hub_recent_matches_shared_movies_plural_txt,
                            member.commonMoviesCount.toString()
                        )
                    },
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8f)
                )
            }
        }
    }
}
