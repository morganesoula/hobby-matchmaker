package com.msoula.hobbymatchmaker.core.common

import android.content.Context
import com.facebook.AccessToken

actual typealias PlatformAccessToken = AccessToken
actual class PlatformContext(val context: Context)
