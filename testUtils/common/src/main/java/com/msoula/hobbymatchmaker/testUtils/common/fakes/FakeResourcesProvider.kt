package com.msoula.hobbymatchmaker.testUtils.common.fakes

class FakeResourcesProvider : StringResourcesProvider {

    override fun getString(stringResId: Int): String {
        return ""
    }
}
