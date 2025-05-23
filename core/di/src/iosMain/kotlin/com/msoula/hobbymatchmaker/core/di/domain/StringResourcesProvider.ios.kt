package com.msoula.hobbymatchmaker.core.di.domain

import platform.Foundation.NSBundle

actual class StringResourcesProvider {
    actual fun getStringByKey(key: String): String {
        return NSBundle.mainBundle.localizedStringForKey(key, null, null)
    }

    actual fun getStringById(resId: Int): String {
        throw UnsupportedOperationException("iOS does not support resource IDs")
    }
}
