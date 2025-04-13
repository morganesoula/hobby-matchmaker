package com.msoula.hobbymatchmaker.core.network

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission

class AndroidNetworkConnectivityChecker(
    private val context: Context
) : NetworkConnectivityChecker {

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override suspend fun hasActiveConnection(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
