package com.solux.luxup.taptap.feature.home.main.model

import androidx.compose.ui.graphics.Color

data class RecentRecord(
    val buttonId: Long,
    val iconRes: Int,
    val iconTint: Color,
    val title: String,
    val lastRecordedAt: String
)