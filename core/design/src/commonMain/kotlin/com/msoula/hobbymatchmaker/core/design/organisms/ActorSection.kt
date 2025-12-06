package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.cast
import com.msoula.hobbymatchmaker.core.design.molecules.ActorItem
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource

@Composable
fun ActorSection(
    modifier: Modifier = Modifier,
    cast: Map<String, String>
) {
    val casting = cast.entries.toList()

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(Res.string.cast),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        SpacerHeight8()
        LazyRow(horizontalArrangement = Arrangement.spacedBy(CustomSize.Eight)) {
            items(casting, key = { it.key }) { (currentNameActor, currentRole) ->
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .semantics { contentDescription = "$currentNameActor, $currentRole" },
                    shape = RoundedCornerShape(CustomSize.Sixteen),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = CustomSize.Four)
                ) {
                    ActorItem(
                        name = currentNameActor,
                        role = currentRole
                    )
                }
            }
        }
    }
}
