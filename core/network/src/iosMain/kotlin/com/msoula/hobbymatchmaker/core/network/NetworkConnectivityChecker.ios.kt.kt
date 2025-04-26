package com.msoula.hobbymatchmaker.core.network

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import network.NetworkReachability
import kotlin.coroutines.resume

class IOSNetworkConnectivityChecker: NetworkConnectivityChecker {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun hasActiveConnection(): Boolean = suspendCancellableCoroutine { cont ->
        NetworkReachability().checkConnection { isConnected ->
            cont.resume(isConnected)
        }
    }
}
