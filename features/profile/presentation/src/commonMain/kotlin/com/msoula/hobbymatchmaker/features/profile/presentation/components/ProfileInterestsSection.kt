package com.msoula.hobbymatchmaker.features.profile.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.icons.Sparkle
import com.msoula.hobbymatchmaker.core.design.user_profile_interests_title
import com.msoula.hobbymatchmaker.features.profile.presentation.models.ProfileMode
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileInterestsSection(
    mode: ProfileMode,
    user: UserProfileUiModel?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row {
                Icon(
                    Sparkle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = stringResource(
                        Res.string.user_profile_interests_title
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))

            InterestsBlock(
                mode = mode,
                interests = user?.interests
            )
        }
    }
}
