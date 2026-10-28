package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.icons.LucideAtSign
import com.msoula.hobbymatchmaker.core.design.icons.MaterialIconsClose
import com.msoula.hobbymatchmaker.core.design.molecules.IconBadge
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

/**
 * Full-page onboarding card: header (badge, title, subtitle), a form slot, an actions slot
 * and an optional caption, inside a glowing rounded card. A close button sits next to the badge
 * when [onClose] is set.
 */
@Composable
fun PickPseudoLayout(
    padding: PaddingValues,
    title: String,
    subtitle: String,
    form: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    caption: String? = null,
    closeContentDescription: String? = null,
    onClose: (() -> Unit)? = null
) {
    val primary = MaterialTheme.colorScheme.primary
    val cardShape = RoundedCornerShape(CustomSize.ThirtyTwo)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(CustomSize.Sixteen)
                .background(
                    Brush.verticalGradient(
                        listOf(primary.copy(alpha = .08f), MaterialTheme.colorScheme.surface)
                    ),
                    cardShape
                )
                .border(1.dp, primary.copy(alpha = .2f), cardShape)
                .padding(CustomSize.TwentyFour),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconBadge(
                    icon = LucideAtSign,
                    modifier = Modifier.align(Alignment.Center)
                )
                onClose?.let {
                    IconButton(
                        onClick = { it() },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = MaterialIconsClose,
                            contentDescription = closeContentDescription,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            form()
            actions()

            caption?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
