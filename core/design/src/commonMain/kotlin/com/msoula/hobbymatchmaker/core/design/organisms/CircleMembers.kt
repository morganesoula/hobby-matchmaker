package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.models.CircleMember
import com.msoula.hobbymatchmaker.core.design.molecules.CircleMemberCard
import com.msoula.hobbymatchmaker.core.design.molecules.EmptyCircleSlotCard
import com.msoula.hobbymatchmaker.core.design.social_circle_section_title
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun CircleMembersSection(
    members: ImmutableList<CircleMember>,
    maxMembers: Int
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = CustomSize.Sixteen),
        verticalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
    ) {
        item(key = "header") {
            SectionHeader(title = stringResource(Res.string.social_circle_section_title))
        }

        items(members, key = { it.uid }) { member ->
            CircleMemberCard(
                memberName = member.name,
                memberPseudo = member.pseudo,
                memberAvatarUrl = member.avatarUrl,
                commonMoviesCount = member.commonMoviesCount
            )
        }

        val emptySlots = (maxMembers - members.size).coerceAtLeast(0)
        items(emptySlots, key = { "slot-$it" }) {
            EmptyCircleSlotCard()
        }
    }
}
