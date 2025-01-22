package com.msoula.hobbymatchmaker.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
