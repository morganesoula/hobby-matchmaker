package com.msoula.hobbymatchmaker.features.social.domain.models

enum class ReportReason {
    SPAM, HARASSMENT, FAKE_PROFILE, INAPPROPRIATE_CONTENT, OTHER
}

data class UserReportDomainModel(
    val reporterUid: String,
    val reportedUid: String,
    val reason: ReportReason,
    val note: String? = null
)
