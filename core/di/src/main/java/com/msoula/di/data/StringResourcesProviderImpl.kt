package com.msoula.di.data

import android.content.Context
import com.msoula.di.domain.StringResourcesProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringResourcesProviderImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : StringResourcesProvider {
        override fun getString(stringResId: Int): String = context.getString(stringResId)
    }
