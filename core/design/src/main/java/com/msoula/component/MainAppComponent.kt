package com.msoula.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeaderTextComponent(
    modifier: Modifier = Modifier,
    text: String,
) {
    Spacer(modifier = modifier.height(40.dp))

    Text(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        text = text,
        fontSize = 28.sp,
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = modifier.height(40.dp))
}
