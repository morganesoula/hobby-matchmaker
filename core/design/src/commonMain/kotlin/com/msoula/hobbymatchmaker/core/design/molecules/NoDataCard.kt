package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.SecondaryButton
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

@Composable
fun NoDataCard(
    modifier: Modifier = Modifier,
    noDataTitle: String,
    noDataText: String,
    noDataBtnText: String,
    onNoDataButtonClicked: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth()
            .padding(start = CustomSize.Eight, end = CustomSize.Eight),
        shape = RoundedCornerShape(CustomSize.Sixteen)
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                noDataTitle,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            SpacerHeight4()
            Text(
                noDataText,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            SpacerHeight4()
            SecondaryButton(
                text = noDataBtnText,
                onClick = { onNoDataButtonClicked() }
            )
        }
    }
}
