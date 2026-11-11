package com.msoula.hobbymatchmaker.core.common

// Read from the platform at runtime so the Settings screen can never show a stale version
interface AppInfo {
    val versionName: String
    val buildNumber: String
}
