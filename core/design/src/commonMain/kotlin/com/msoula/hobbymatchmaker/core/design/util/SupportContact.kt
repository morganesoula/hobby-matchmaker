package com.msoula.hobbymatchmaker.core.design.util

// Public contact shown in the app and on the Play Store pages (privacy policy, child safety standards)
const val SUPPORT_EMAIL = "morganes56@gmail.com"

// Hosted on Firebase Hosting; Google Play requires the policy to be reachable from inside the app
const val PRIVACY_POLICY_URL = "https://hobby-matchmaker.web.app/privacy.html"

fun supportMailtoUri(subject: String): String =
    "mailto:$SUPPORT_EMAIL?subject=${subject.replace(" ", "%20")}"
