package com.msoula.hobbymatchmaker.features.social.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.organisms.ReceivedInvitations
import com.msoula.hobbymatchmaker.core.design.organisms.SentInvitations
import com.msoula.hobbymatchmaker.core.design.templates.SocialLayout
import com.msoula.hobbymatchmaker.core.navigation.presentation.Destination

@Composable
fun SocialContent(
    modifier: Modifier = Modifier,
    socialViewModel: SocialViewModel,
    destinations: List<Destination>,
    onNavigate: (String) -> Unit
) {
    Scaffold { paddingValues ->
        SocialLayout(
            paddingValues = paddingValues,
            destinations = destinations,
            receivedContent = {
                ReceivedInvitations()
            },
            sentContent = {
                SentInvitations()
            }
        )
    }
}
