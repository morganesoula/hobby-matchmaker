package com.msoula.hobbymatchmaker.core.design.util

import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.social_sent_requests_status_accepted
import com.msoula.hobbymatchmaker.core.design.social_sent_requests_status_declined
import com.msoula.hobbymatchmaker.core.design.social_sent_requests_status_pending
import org.jetbrains.compose.resources.stringResource

@Composable
fun String.asInviteStatusText(): String {
    return when (this.lowercase()) {
        "pending" -> stringResource(Res.string.social_sent_requests_status_pending)
        "accepted" -> stringResource(Res.string.social_sent_requests_status_accepted)
        else -> stringResource(Res.string.social_sent_requests_status_declined)
    }
}
