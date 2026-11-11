package com.msoula.hobbymatchmaker.core.common

import platform.Foundation.NSBundle

class IosAppInfo : AppInfo {
    private val info = NSBundle.mainBundle.infoDictionary

    override val versionName: String = info?.get("CFBundleShortVersionString") as? String ?: ""
    override val buildNumber: String = info?.get("CFBundleVersion") as? String ?: ""
}
