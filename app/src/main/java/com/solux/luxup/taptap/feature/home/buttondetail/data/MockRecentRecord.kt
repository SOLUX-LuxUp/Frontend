package com.solux.luxup.taptap.feature.home.buttondetail.data

import com.solux.luxup.taptap.feature.home.buttondetail.model.RecentRecord
import com.solux.luxup.taptap.feature.home.buttondetail.model.RecentRecordResponse

val mockRecentRecord = RecentRecord(
    buttonId = 1L,
    lastRecordedAt = "2025-05-28T09:00:00Z",
    elapsedSeconds = 5400L
)

val mockRecentRecordResponse = RecentRecordResponse(
    success = true,
    data = mockRecentRecord
)