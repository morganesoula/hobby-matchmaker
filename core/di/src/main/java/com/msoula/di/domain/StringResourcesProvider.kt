package com.msoula.di.domain

import androidx.annotation.StringRes

interface StringResourcesProvider {
    fun getString(
        @StringRes stringResId: Int,
    ): String
}
