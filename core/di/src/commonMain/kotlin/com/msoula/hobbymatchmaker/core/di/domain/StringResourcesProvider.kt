package com.msoula.hobbymatchmaker.core.di.domain

expect class StringResourcesProvider {
    fun getStringByKey(key: String): String
    fun getStringById(resId: Int): String
}
