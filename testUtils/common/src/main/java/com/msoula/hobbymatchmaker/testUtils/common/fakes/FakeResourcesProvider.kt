package com.msoula.hobbymatchmaker.testUtils.common.fakes

import com.msoula.hobbymatchmaker.core.di.domain.StringResourcesProvider

class FakeResourcesProvider : StringResourcesProvider {

    override fun getString(stringResId: Int): String {
        return ""
    }
}
