package com.msoula.hobbymatchmaker.core.authentication.domain.models

data class UserDomainModel(
    val isConnected: Boolean
) {
    companion object {
        const val DEFAULT_IS_CONNECTED: Boolean = false
    }
}
