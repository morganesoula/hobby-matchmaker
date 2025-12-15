package com.msoula.hobbymatchmaker.core.login.presentation.signIn.models

import androidx.compose.runtime.Immutable
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient

@Immutable
data class SocialClientsVM(
    val clients: Map<ProviderType, SocialUIClient>
)
