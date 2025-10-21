package com.msoula.hobbymatchmaker.core.network

interface NetworkConnectivityChecker {
    fun hasActiveConnection(): Boolean
}
