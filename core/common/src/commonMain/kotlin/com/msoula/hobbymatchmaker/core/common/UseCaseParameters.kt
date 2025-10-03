package com.msoula.hobbymatchmaker.core.common

sealed class Parameters {
    data class StringParam(val value: String) : Parameters()
    data class DoubleStringParam(val firstValue: String, val secondValue: String) : Parameters()
}
