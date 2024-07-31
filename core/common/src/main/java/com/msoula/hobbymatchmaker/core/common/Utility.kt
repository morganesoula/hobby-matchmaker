package com.msoula.hobbymatchmaker.core.common

import java.util.Locale

fun getDeviceLocale(): String {
    val locale = Locale.getDefault()
    return "${locale.language}-${locale.country}"
}
