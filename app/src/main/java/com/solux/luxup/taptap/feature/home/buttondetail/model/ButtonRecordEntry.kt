package com.solux.luxup.taptap.feature.home.buttondetail.model

data class ButtonRecordEntry(
    val recordId: Long,
    val recordedAt: String, // ISO-8601 (UTC), 예: "2026-07-21T02:37:00Z"
    val memo: String? = null,
    val emoji: String? = null
)