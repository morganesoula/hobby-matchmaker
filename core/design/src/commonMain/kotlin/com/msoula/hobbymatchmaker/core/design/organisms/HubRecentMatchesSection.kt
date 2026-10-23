package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_no_data_btn_txt
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_no_data_title
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_no_data_txt
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_subtitle
import com.msoula.hobbymatchmaker.core.design.hub_recent_matches_title
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsPerson_add
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
import com.msoula.hobbymatchmaker.core.design.molecules.MemberItem
import com.msoula.hobbymatchmaker.core.design.molecules.NoDataCard
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun HubRecentMatchesSection(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    HubSection(
        modifier = modifier,
        title = stringResource(Res.string.hub_recent_matches_title),
        subtitle = stringResource(Res.string.hub_recent_matches_subtitle),
        content = content
    )
}

@Composable
fun HubRecentMatches(
    modifier: Modifier = Modifier,
    members: ImmutableList<ProfileSocialMember>,
    onMemberClicked: (member: ProfileSocialMember) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = CustomSize.Sixteen),
        horizontalArrangement = Arrangement.spacedBy(CustomSize.Sixteen),
        verticalAlignment = Alignment.Top
    ) {
        items(members, key = { it.uid }) { member ->
            MemberItem(
                member = member,
                avatarSize = CustomSize.SixtyFour + CustomSize.Sixteen,
                avatarBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = .3f),
                onMemberClicked = onMemberClicked
            )
        }
    }
}

@Composable
fun HubNoRecentMatchesSection(
    modifier: Modifier = Modifier,
    navigateToProfileScreen: () -> Unit
) {
    // Section only gets 1/3 of the screen: let the card scroll rather than clip on small devices
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        NoDataCard(
            modifier = Modifier.fillMaxWidth(),
            noDataTitle = stringResource(Res.string.hub_recent_matches_no_data_title),
            noDataText = stringResource(Res.string.hub_recent_matches_no_data_txt),
            noDataBtnText = stringResource(Res.string.hub_recent_matches_no_data_btn_txt),
            icon = MaterialSymbolsPerson_add,
            onNoDataButtonClicked = navigateToProfileScreen,
        )
    }
}
