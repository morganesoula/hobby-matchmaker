package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.error_issue_retry
import com.msoula.hobbymatchmaker.core.design.models.EmptyStateConfig
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.util.RetryPolicy
import com.msoula.hobbymatchmaker.core.design.util.UIErrorHint
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiState
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T> StateContainer(
    state: UiState<T>,
    onLoading: @Composable () -> Unit = { LoadingCircularProgress() },
    onEmpty: @Composable () -> Unit = { },
    onError: @Composable (UIText, UIErrorHint) -> Unit = { error, hint ->
        ErrorStateScreen(
            error, hint
        )
    },
    onSuccess: @Composable (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> onLoading()
        is UiState.Empty -> onEmpty()
        is UiState.Error -> onError(state.error, state.hint)
        is UiState.Success -> onSuccess(state.data)
    }
}

@Composable
fun ErrorStateScreen(
    error: UIText,
    hint: UIErrorHint,
    onRetry: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(CustomSize.TwentyFour)
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(72.dp)
        )
        SpacerHeight16()
        Text(text = error.asString(), textAlign = TextAlign.Center)

        if (hint.retry != RetryPolicy.Never && onRetry != null) {
            SpacerHeight16()
            Button(onClick = onRetry) {
                Text(text = stringResource(Res.string.error_issue_retry))
            }
        }
    }
}

@Composable
fun EmptyStateScreen(config: EmptyStateConfig) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(CustomSize.TwentyFour)
    ) {
        Icon(
            imageVector = config.icon,
            contentDescription = null,
            modifier = Modifier.size(96.dp)
        )
        SpacerHeight16()
        Text(config.title.asString(), style = MaterialTheme.typography.titleMedium)

        config.description?.let {
            SpacerHeight8()
            Text(it.asString(), textAlign = TextAlign.Center)
        }

        config.ctaAction?.let {
            SpacerHeight16()
            Button(onClick = it) {
                Text(config.ctaText?.asString() ?: "Action")
            }
        }
    }
}


