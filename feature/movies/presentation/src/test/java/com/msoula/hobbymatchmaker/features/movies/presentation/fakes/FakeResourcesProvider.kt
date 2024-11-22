package com.msoula.hobbymatchmaker.features.movies.presentation.fakes

import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider

class FakeResourcesProvider : StringResourcesProvider {

    override fun getString(stringResId: Int): String {
        return ""
    }
}
