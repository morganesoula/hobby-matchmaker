package com.msoula.hobbymatchmaker.core.di.domain

import androidx.annotation.StringRes

interface StringResourcesProvider {
    fun getString(
        @StringRes stringResId: Int,
    ): String
}
