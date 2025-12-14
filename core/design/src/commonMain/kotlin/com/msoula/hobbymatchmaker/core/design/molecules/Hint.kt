package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_requirements_title
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapCheck
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapPersonExclamation
import com.msoula.hobbymatchmaker.core.design.icons.FeatherXCircle
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import org.jetbrains.compose.resources.stringResource

@Composable
fun TipTextField(
    modifier: Modifier = Modifier,
    hintText: String,
    icon: ImageVector,
    iconColor: Color? = null,
    textColor: Color? = null
) {
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = .1f),
                RoundedCornerShape(
                    CustomSize.Eight
                )
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = .3f),
                RoundedCornerShape(CustomSize.Eight)
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(start = CustomSize.Sixteen).size(IconSize.Sixteen),
            tint = iconColor ?: MaterialTheme.colorScheme.primary
        )
        Text(
            text = hintText,
            color = textColor ?: MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                vertical = CustomSize.Sixteen,
                horizontal = CustomSize.Sixteen
            ),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun WarningTextField(
    modifier: Modifier = Modifier,
    warningText: String
) {
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.errorContainer,
                RoundedCornerShape(
                    CustomSize.Eight
                )
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.error,
                RoundedCornerShape(CustomSize.Eight)
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = BootstrapPersonExclamation,
            contentDescription = null,
            modifier = Modifier.padding(start = CustomSize.Sixteen).size(IconSize.Sixteen),
            tint = MaterialTheme.colorScheme.onErrorContainer
        )
        Text(
            text = warningText,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(
                vertical = CustomSize.Sixteen,
                horizontal = CustomSize.Sixteen
            ),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

data class ValidationRequirement(
    val text: String,
    val isValid: Boolean
)

@Composable
fun ValidationRequirementItem(
    requirement: ValidationRequirement,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = CustomSize.Four),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(CustomSize.Eight)
    ) {
        Icon(
            imageVector = if (requirement.isValid) BootstrapCheck else FeatherXCircle,
            contentDescription = if (requirement.isValid) "Valid" else "Invalid",
            tint = if (requirement.isValid) MaterialTheme.colorScheme.tertiary
            else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(IconSize.Sixteen)
        )
        Text(
            text = requirement.text,
            style = MaterialTheme.typography.bodySmall,
            color = if (requirement.isValid) MaterialTheme.colorScheme.tertiary
            else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun ValidationRequirementsList(
    requirements: List<ValidationRequirement>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                RoundedCornerShape(CustomSize.Eight)
            )
            .padding(CustomSize.Sixteen)
    ) {
        Text(
            text = stringResource(Res.string.edit_profile_basic_information_requirements_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SpacerHeight8()
        requirements.forEach { requirement ->
            ValidationRequirementItem(requirement = requirement)
        }
    }
}
