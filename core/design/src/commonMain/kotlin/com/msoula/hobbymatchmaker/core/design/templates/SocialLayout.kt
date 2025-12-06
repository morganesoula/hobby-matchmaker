package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.msoula.hobbymatchmaker.core.design.models.TabItem

@Composable
fun SocialLayout(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    tabs: List<TabItem>,
    receivedContent: @Composable () -> Unit,
    sentContent: @Composable () -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = modifier.padding(paddingValues)) {
        PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = tab.label,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    icon = {
                        androidx.compose.material3.Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.contentDescription
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> receivedContent()
            1 -> sentContent()
        }
    }
}
