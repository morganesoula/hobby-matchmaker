package com.msoula.hobbymatchmaker.features.social.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.models.TabItem
import com.msoula.hobbymatchmaker.core.design.organisms.ReceivedInvitations
import com.msoula.hobbymatchmaker.core.design.organisms.SentInvitations
import com.msoula.hobbymatchmaker.core.design.templates.SocialLayout

@Composable
fun SocialContent(
    modifier: Modifier = Modifier,
    socialViewModel: SocialViewModel,
    tabs: List<TabItem>,
    onNavigate: (String) -> Unit
) {
    Scaffold { paddingValues ->
        SocialLayout(
            paddingValues = paddingValues,
            tabs = tabs,
            receivedContent = {
                ReceivedInvitations()
            },
            sentContent = {
                SentInvitations()
            }
        )
    }
}
