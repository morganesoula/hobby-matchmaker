package com.msoula.hobbymatchmaker.core.common

import kotlinx.cinterop.ExperimentalForeignApi

class IOSNetworkConnectivityChecker : NetworkConnectivityChecker {
    @OptIn(ExperimentalForeignApi::class)
    override fun hasActiveConnection(): Boolean =
        NetworkReachability().isConnected()
}
