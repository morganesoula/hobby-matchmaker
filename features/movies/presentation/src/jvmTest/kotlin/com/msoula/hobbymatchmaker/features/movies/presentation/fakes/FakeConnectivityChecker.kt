package com.msoula.hobbymatchmaker.features.movies.presentation.fakes

import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker

class FakeConnectivityChecker(private val hasNetwork: Boolean = true) : NetworkConnectivityChecker {
    override suspend fun hasActiveConnection(): Boolean = hasNetwork
}
