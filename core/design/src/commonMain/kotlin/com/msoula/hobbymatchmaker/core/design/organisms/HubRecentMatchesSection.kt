package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.MainTitle
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_no_data_btn_txt
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_no_data_title
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_no_data_txt
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_title
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsPerson_add
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
import com.msoula.hobbymatchmaker.core.design.molecules.MemberItem
import com.msoula.hobbymatchmaker.core.design.molecules.NoDataCard
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun HubRecentMatches(
    modifier: Modifier = Modifier,
    members: ImmutableList<ProfileSocialMember>,
    onMemberClicked: (member: ProfileSocialMember) -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = CustomSize.Sixteen)
    ) {
        MainTitle(
            modifier = Modifier.fillMaxWidth().padding(top = CustomSize.Eight),
            text = stringResource(Res.string.hub_recent_matches_title)
        )
        SpacerHeight8()

        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(members) { member ->
                MemberItem(
                    member = member,
                    onMemberClicked = onMemberClicked
                )
            }
        }
    }
}

@Composable
fun HubNoRecentMatchesSection(
    modifier: Modifier = Modifier,
    navigateToProfileScreen: () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = CustomSize.Sixteen)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth()
                .padding(top = CustomSize.Sixteen),
            text = stringResource(Res.string.hub_recent_matches_title),
            style = MaterialTheme.typography.titleLarge
        )
        SpacerHeight8()

        NoDataCard(
            noDataTitle = stringResource(Res.string.hub_recent_matches_no_data_title),
            noDataText = stringResource(Res.string.hub_recent_matches_no_data_txt),
            noDataBtnText = stringResource(Res.string.hub_recent_matches_no_data_btn_txt),
            icon = MaterialSymbolsPerson_add,
            onNoDataButtonClicked = navigateToProfileScreen,
        )
    }
}
