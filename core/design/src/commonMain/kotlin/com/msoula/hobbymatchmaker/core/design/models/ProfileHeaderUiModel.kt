package com.msoula.hobbymatchmaker.core.design.models

data class ProfileHeaderUiModel(
    val name: String = "",
    val pseudo: String = "",
    val biography: String = "",
    val circleSize: Int = 0,
    val avatarUrl: String? = null
)
