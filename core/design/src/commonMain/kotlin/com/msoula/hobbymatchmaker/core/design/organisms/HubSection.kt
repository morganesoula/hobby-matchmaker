package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.MainTitle
import com.msoula.hobbymatchmaker.core.design.atoms.MediumTitle
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

/**
 * Hub section: header always visible, [content] (loading / error / empty / data) fills the rest.
 */
@Composable
fun HubSection(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    content: @Composable BoxScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = CustomSize.Sixteen)) {
            MainTitle(modifier = Modifier.fillMaxWidth(), text = title)
            SpacerHeight4()
            MediumTitle(modifier = Modifier.fillMaxWidth(), text = subtitle)
        }
        SpacerHeight8()
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}
