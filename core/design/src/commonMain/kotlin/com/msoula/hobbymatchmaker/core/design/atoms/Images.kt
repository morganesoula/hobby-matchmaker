package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.ic_movie_clapper_board
import com.msoula.hobbymatchmaker.core.design.ic_no_image_found_playstore
import com.msoula.hobbymatchmaker.core.design.icons.Delete
import com.msoula.hobbymatchmaker.core.design.icons.Hide_image
import com.msoula.hobbymatchmaker.core.design.icons.Person
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ActorIcon(
    modifier: Modifier = Modifier,
    contentDescription: String,
    imageVector: ImageVector? = null
) {
    Icon(
        imageVector = imageVector ?: Person,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.size(CustomSize.ThirtyTwo)
    )
}

@Composable
fun LoadingPosterPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_movie_clapper_board),
            contentDescription = null,
            modifier = Modifier.size(48.dp).alpha(0.85f),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorPosterPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_no_image_found_playstore),
            contentDescription = null,
            modifier = Modifier.size(48.dp).alpha(0.85f),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun HMMShimmerEffect(
    modifier: Modifier = Modifier
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(5))
                .shimmerEffect(),
    )
}

fun Modifier.shimmerEffect(): Modifier =
    composed {
        var size by remember {
            mutableStateOf(IntSize.Zero)
        }

        val transition = rememberInfiniteTransition(label = "")
        val startOffsetX by transition.animateFloat(
            initialValue = -2 * size.width.toFloat(),
            targetValue = 2 * size.width.toFloat(),
            animationSpec =
                infiniteRepeatable(
                    animation = tween(1000),
                ),
            label = "",
        )

        background(
            brush =
                Brush.linearGradient(
                    colors =
                        listOf(
                            Color(0xFFB8B5B5),
                            Color(0xFF8F8B8B),
                            Color(0xFFB8B5B5),
                        ),
                    start = Offset(startOffsetX, 0f),
                    end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat()),
                ),
        )
            .onGloballyPositioned {
                size = it.size
            }
    }

@Composable
fun CircleWithIcon(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    icon: ImageVector,
    iconTint: Color,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(CustomSize.NinetySix)
            .background(
                color = backgroundColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(IconSize.FortyEight)
        )
    }
}

@Composable
fun RectangleWithIcon(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    icon: ImageVector,
    iconTint: Color,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(CustomSize.FortyEight)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(CustomSize.Eight)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(IconSize.TwentyFour)
        )
    }
}

@Composable
fun CircleWithCustomPhoto(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    contentDescription: String? = null,
    image: ImageVector? = null
) {
    Box(
        modifier = modifier
            .size(CustomSize.NinetySix)
            .background(
                color = backgroundColor ?: MaterialTheme.colorScheme.onSurface,
                shape = CircleShape
            )
            .border(2.dp, borderColor ?: MaterialTheme.colorScheme.surface, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = image ?: Hide_image,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(IconSize.FortyEight).padding(CustomSize.Four)
        )
    }
}

@Composable
fun FormIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    size: Dp,
    tint: Color,
    contentDescription: String? = null
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier.size(size = size),
        tint = tint
    )
}

@Preview
@Composable
fun FormIconPreview() {
    FormIcon(
        icon = Delete,
        size = IconSize.TwentyFour,
        tint = MaterialTheme.colorScheme.primary
    )
}
