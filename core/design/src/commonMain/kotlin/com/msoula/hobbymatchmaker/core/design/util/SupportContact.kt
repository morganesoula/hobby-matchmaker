package com.msoula.hobbymatchmaker.core.design.util

// Public contact shown in the app and on the Play Store pages (privacy policy, child safety standards)
const val SUPPORT_EMAIL = "morganes56@gmail.com"

fun supportMailtoUri(subject: String): String =
    "mailto:$SUPPORT_EMAIL?subject=${subject.replace(" ", "%20")}"
