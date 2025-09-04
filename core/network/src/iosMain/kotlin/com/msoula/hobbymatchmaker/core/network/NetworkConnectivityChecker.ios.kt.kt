package com.msoula.hobbymatchmaker.core.network

import kotlinx.cinterop.ExperimentalForeignApi
import network.NetworkReachability

class IOSNetworkConnectivityChecker : NetworkConnectivityChecker {
    @OptIn(ExperimentalForeignApi::class)
    override fun hasActiveConnection(): Boolean =
        NetworkReachability().checkConnection { it }
}

