package com.msoula.hobbymatchmaker.core.common

import java.util.Locale

actual fun getDeviceLocale(): String {
    val locale = Locale.getDefault()
    return "${locale.language}-${locale.country}"
}

actual fun isIosPlatform(): Boolean =
    false
