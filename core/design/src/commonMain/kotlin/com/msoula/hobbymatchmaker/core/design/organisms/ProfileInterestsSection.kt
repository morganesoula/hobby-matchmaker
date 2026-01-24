package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.molecules.InterestsBlock
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.user_profile_interests_title
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileInterestsSection(
    interests: ImmutableList<String>?
) {
    GenericCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.TwentyFour),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column {
            Text(
                text = stringResource(Res.string.user_profile_interests_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = CustomSize.Eight)
            )
            SpacerHeight16()
            interests?.let {
                InterestsBlock(
                    interests = interests
                )
            }
        }
    }
}
