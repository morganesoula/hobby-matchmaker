package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.msoula.hobbymatchmaker.core.common.formatOneDecimal
import com.msoula.hobbymatchmaker.core.design.icons.MaterialIconsFavorite
import com.msoula.hobbymatchmaker.core.design.icons.MaterialIconsFavorite_border
import com.msoula.hobbymatchmaker.core.design.icons.VscodeCodiconsStarFull
import com.msoula.hobbymatchmaker.core.design.theme.CustomFontSize
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    animateFavorite: Boolean,
    onClick: () -> Unit
) {
    val favoriteScale by animateFloatAsState(
        targetValue = if (animateFavorite) 2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "favorite_scale"
    )

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .background(color = Color.Black.copy(alpha = 0.3f), shape = CircleShape)
            .scale(favoriteScale)
    ) {
        Icon(
            imageVector = if (isFavorite) MaterialIconsFavorite else MaterialIconsFavorite_border,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(IconSize.FortyEight)
        )
    }
}

@Composable
fun RatingChip(
    voteAverage: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(CustomSize.Sixteen)
            )
            .padding(CustomSize.Eight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = VscodeCodiconsStarFull,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(CustomSize.Four))
        Text(
            text = voteAverage.formatOneDecimal(),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun StatusChip(
    modifier: Modifier = Modifier,
    status: String
) {
    Text(
        text = status,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = CustomSize.Eight, vertical = CustomSize.Four),
        fontSize = CustomFontSize.Twelve,
        fontWeight = FontWeight.Bold
    )
}
