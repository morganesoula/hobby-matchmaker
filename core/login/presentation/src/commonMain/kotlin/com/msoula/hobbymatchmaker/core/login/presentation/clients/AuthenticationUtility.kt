package com.msoula.hobbymatchmaker.core.login.presentation.clients

fun generateRandomNonce(length: Int = 32): String {
    val charset = "0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz-._"
    return (1..length)
        .map { charset.random() }
        .joinToString("")
}
