package com.msoula.hobbymatchmaker.core.design.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.CircleWithCustomPhoto
import com.msoula.hobbymatchmaker.core.design.models.MatchAnimationData
import com.msoula.hobbymatchmaker.core.design.models.MatchAnimationStep
import com.msoula.hobbymatchmaker.core.design.social_movie_match_notification
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun MovieMatchAnimation(
    step: MatchAnimationStep,
    updateAnimationStep: (MatchAnimationStep) -> Unit,
    matchAnimationData: MatchAnimationData,
    onFinished: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = .55f)),
        contentAlignment = Alignment.Center
    ) {
        val screenWidthPx = constraints.maxWidth.toFloat()

        val leftX = remember { Animatable(-screenWidthPx) }
        val rightX = remember { Animatable(screenWidthPx) }
        val explosionScale = remember { Animatable(0f) }
        val ripple1Scale = remember { Animatable(0f) }
        val ripple1Alpha = remember { Animatable(0f) }
        val ripple2Scale = remember { Animatable(0f) }
        val ripple2Alpha = remember { Animatable(0f) }
        val ripple3Scale = remember { Animatable(0f) }
        val ripple3Alpha = remember { Animatable(0f) }

        LaunchedEffect(step) {
            when (step) {
                MatchAnimationStep.SlidingIn -> {
                    explosionScale.snapTo(0f)
                    ripple1Scale.snapTo(0f)
                    ripple1Alpha.snapTo(0f)
                    ripple2Scale.snapTo(0f)
                    ripple2Alpha.snapTo(0f)
                    ripple3Scale.snapTo(0f)
                    ripple3Alpha.snapTo(0f)

                    coroutineScope {
                        launch {
                            leftX.animateTo(
                                targetValue = 30f,
                                animationSpec = tween(700, easing = FastOutSlowInEasing)
                            )
                            leftX.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(150, easing = FastOutSlowInEasing)
                            )
                        }
                        launch {
                            rightX.animateTo(
                                targetValue = -30f,
                                animationSpec = tween(700, easing = FastOutSlowInEasing)
                            )
                            rightX.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(150, easing = FastOutSlowInEasing)
                            )
                        }
                    }
                    delay(150)
                    updateAnimationStep(MatchAnimationStep.Explosion)
                }

                MatchAnimationStep.Explosion -> {
                    coroutineScope {
                        launch {
                            explosionScale.animateTo(1.2f, tween(200))
                            explosionScale.animateTo(0f, tween(150))
                        }
                        launch {
                            ripple1Alpha.animateTo(.6f, tween(600))
                            ripple1Scale.animateTo(2.5f, tween(400, easing = FastOutSlowInEasing))
                        }
                        launch {
                            delay(100)
                            ripple1Alpha.animateTo(0f, tween(300))
                        }
                        launch {
                            delay(100)
                            ripple2Alpha.animateTo(.4f, tween(100))
                            ripple2Scale.animateTo(3.5f, tween(450, easing = FastOutSlowInEasing))
                        }
                        launch {
                            delay(200)
                            ripple2Alpha.animateTo(0f, tween(350))
                        }
                        launch {
                            delay(200)
                            ripple3Alpha.animateTo(.25f, tween(100))
                            ripple3Scale.animateTo(4.5f, tween(500, easing = FastOutSlowInEasing))
                        }
                        launch {
                            delay(350)
                            ripple3Alpha.animateTo(0f, tween(350))
                        }
                    }
                    updateAnimationStep(MatchAnimationStep.SlidingOut)
                }

                MatchAnimationStep.SlidingOut -> {
                    coroutineScope {
                        launch {
                            leftX.animateTo(-screenWidthPx, tween(400))
                        }
                        launch {
                            rightX.animateTo(screenWidthPx, tween(400))
                        }
                    }
                    updateAnimationStep(MatchAnimationStep.ShowText)
                }

                MatchAnimationStep.ShowText -> {
                    delay(2500)
                    onFinished()
                }

                else -> Unit
            }
        }

        CircleWithCustomPhoto(
            modifier = Modifier.offset {
                IntOffset(leftX.value.roundToInt(), 0)
            },
            customAvatarPath = matchAnimationData.ownerAvatarUrl
        )

        Row(
            modifier = Modifier.offset {
                IntOffset(rightX.value.roundToInt(), 0)
            }
        ) {
            repeat(matchAnimationData.matchingMembers.size) { index ->
                CircleWithCustomPhoto(
                    modifier = Modifier
                        .offset(x = (-12 * index).dp),
                    customAvatarPath = matchAnimationData.matchingMembers[index].avatarUrl
                )
            }
        }

        Box(
            modifier = Modifier
                .size(CustomSize.SixtyFour)
                .scale(ripple3Scale.value)
                .alpha(ripple3Alpha.value)
                .background(
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(CustomSize.SixtyFour)
                .scale(ripple2Scale.value)
                .alpha(ripple2Alpha.value)
                .background(
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(CustomSize.SixtyFour)
                .scale(ripple1Scale.value)
                .alpha(ripple1Alpha.value)
                .background(
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(CustomSize.SixtyFour)
                .scale(explosionScale.value)
                .background(
                    Color(0xFFFFD700).copy(alpha = .9f),
                    CircleShape
                )
        )

        AnimatedVisibility(
            visible = step == MatchAnimationStep.ShowText,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = CustomSize.TwentyFour)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = .9f),
                        shape = RoundedCornerShape(CustomSize.Sixteen)
                    )
                    .padding(CustomSize.Sixteen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(
                        Res.string.social_movie_match_notification,
                        matchAnimationData.matchingMemberNames
                    ),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
