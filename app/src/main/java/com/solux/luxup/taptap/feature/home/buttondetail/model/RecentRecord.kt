package com.solux.luxup.taptap.feature.home.buttondetail.model

data class RecentRecordResponse(
    val success: Boolean,
    val data: RecentRecord
)

data class RecentRecord(
    val buttonId: Long,
    val lastRecordedAt: String, // ISO-8601 (UTC), 예: "2025-05-28T09:00:00Z"
    val elapsedSeconds: Long
)