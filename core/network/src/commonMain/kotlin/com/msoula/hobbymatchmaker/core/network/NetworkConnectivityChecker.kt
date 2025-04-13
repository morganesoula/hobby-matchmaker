package com.msoula.hobbymatchmaker.core.network

interface NetworkConnectivityChecker {
    suspend fun hasActiveConnection(): Boolean
}
