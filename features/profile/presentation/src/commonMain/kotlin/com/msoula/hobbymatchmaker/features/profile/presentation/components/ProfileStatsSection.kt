package com.msoula.hobbymatchmaker.features.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.movies_liked_count_title
import com.msoula.hobbymatchmaker.core.design.social_members_count_title
import com.msoula.hobbymatchmaker.features.profile.presentation.models.ProfileMode
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileStatsSection(
    mode: ProfileMode,
    user: UserProfileUiModel?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.Movie,
                value = "${user?.moviesLikedCount}",
                label = stringResource(Res.string.movies_liked_count_title)
            )

            StatItem(
                icon = Icons.Default.People,
                value = "${user?.socialMembersCount}/5",
                label = stringResource(Res.string.social_members_count_title)
            )
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    val color = MaterialTheme.colorScheme.primary

    Row(
        Modifier
            .background(color.copy(alpha = .15f), RoundedCornerShape(8.dp))
            .padding(all = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(value, fontWeight = FontWeight.Medium, color = Color.Black)
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }

}
