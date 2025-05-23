package com.msoula.hobbymatchmaker.core.login.presentation.clients

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH

@OptIn(ExperimentalForeignApi::class)
fun sha256(input: String): String {
    val data = input.encodeToByteArray()
    val hash = UByteArray(CC_SHA256_DIGEST_LENGTH)

    data.usePinned { pinned ->
        hash.usePinned { hashPinned ->
            CC_SHA256(pinned.addressOf(0), data.size.convert(), hashPinned.addressOf(0))
        }
    }

    return hash.joinToString("") { it.toString(16).padStart(2, '0') }
}
