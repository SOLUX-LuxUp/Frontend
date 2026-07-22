package com.solux.luxup.taptap.feature.notification.model

import androidx.compose.ui.graphics.Color

data class NotificationItem(
    val id: Long,
    val title: String,
    val category: String,
    val iconRes: Int,
    val iconTint: Color,
    val scheduleText: String,
    val isEnabled: Boolean
)