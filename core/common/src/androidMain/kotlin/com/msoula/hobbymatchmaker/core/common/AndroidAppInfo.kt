package com.msoula.hobbymatchmaker.core.common

import android.content.Context

class AndroidAppInfo(context: Context) : AppInfo {
    private val packageInfo = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0)
    }.getOrNull()

    override val versionName: String = packageInfo?.versionName.orEmpty()

    @Suppress("DEPRECATION")
    override val buildNumber: String = packageInfo?.versionCode?.toString().orEmpty()
}
