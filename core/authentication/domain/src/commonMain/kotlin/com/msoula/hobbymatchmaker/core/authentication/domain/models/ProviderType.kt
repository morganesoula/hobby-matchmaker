package com.msoula.hobbymatchmaker.core.authentication.domain.models

enum class ProviderType(val className: String) {
    GOOGLE("GoogleAuthProvider"),
    FACEBOOK("FacebookAuthProvider"),
    APPLE("AppleAuthProvider")
}
