package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.MediumTitle
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_no_data_btn_txt
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_no_data_title
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_no_data_txt
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_title
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
import com.msoula.hobbymatchmaker.core.design.molecules.MemberItem
import com.msoula.hobbymatchmaker.core.design.molecules.NoDataCard
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun HubRecentMatches(
    modifier: Modifier = Modifier,
    members: ImmutableList<ProfileSocialMember>,
    navigateToProfileScreen: () -> Unit
) {
    Column {
        MediumTitle(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.hub_recent_matches_title)
        )
        SpacerHeight8()

        if (members.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(members) { member ->
                    MemberItem(member)
                }
            }
        } else {
            Card {
                NoDataCard(
                    noDataTitle = stringResource(Res.string.hub_recent_matches_no_data_title),
                    noDataText = stringResource(Res.string.hub_recent_matches_no_data_txt),
                    noDataBtnText = stringResource(Res.string.hub_recent_matches_no_data_btn_txt),
                    onNoDataButtonClicked = navigateToProfileScreen
                )
            }
        }
    }
}
