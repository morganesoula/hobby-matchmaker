package com.msoula.hobbymatchmaker.core.common

import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun getDeviceLocale(): String {
    val language = NSLocale.currentLocale.languageCode
    val countryCode = NSLocale.currentLocale.countryCode
    return "$language-$countryCode"
}

actual fun isIosPlatform(): Boolean = true
