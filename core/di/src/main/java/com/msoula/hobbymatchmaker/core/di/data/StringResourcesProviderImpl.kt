package com.msoula.hobbymatchmaker.core.di.data

import android.content.Context
import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider

class StringResourcesProviderImpl(
    private val context: Context
): StringResourcesProvider {
    override fun getString(stringResId: Int): String {
        return context.getString(stringResId)
    }
}
