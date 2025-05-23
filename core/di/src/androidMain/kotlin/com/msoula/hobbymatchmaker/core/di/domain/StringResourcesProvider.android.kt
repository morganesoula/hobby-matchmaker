package com.msoula.hobbymatchmaker.core.di.domain

import android.content.Context
import androidx.annotation.StringRes

actual class StringResourcesProvider(
    private val context: Context
) {
    actual fun getStringByKey(key: String): String {
        val resourceId = context.resources.getIdentifier(key, "string", context.packageName)
        if (resourceId == 0) {
            throw IllegalArgumentException("String resource not found for key: $key")
        }

        return context.getString(resourceId)
    }

    actual fun getStringById(@StringRes resId: Int): String {
        return context.getString(resId)
    }
}
