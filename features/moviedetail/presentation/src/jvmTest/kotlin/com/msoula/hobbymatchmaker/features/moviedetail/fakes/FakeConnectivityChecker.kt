package com.msoula.hobbymatchmaker.features.moviedetail.fakes

import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker

class FakeConnectivityChecker(private var active: Boolean = true) : NetworkConnectivityChecker {
    override suspend fun hasActiveConnection(): Boolean = active
}
