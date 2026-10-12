package com.msoula.hobbymatchmaker.core.common

interface NetworkConnectivityChecker {
    fun hasActiveConnection(): Boolean
}
