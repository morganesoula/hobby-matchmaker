package com.msoula.hobbymatchmaker.core.authentication.domain.models

enum class ProviderType(val id: String) {
    GOOGLE("google.com"),
    FACEBOOK("facebook.com"),
    APPLE("apple.com"),
    EMAIL("password")
}
