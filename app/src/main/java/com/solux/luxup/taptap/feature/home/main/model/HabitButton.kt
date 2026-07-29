package com.solux.luxup.taptap.feature.home.main.model

import androidx.compose.ui.graphics.Color

data class HabitButton(
    val buttonId: Long,
    val title: String,
    val category: String,
    val categoryId: Long?,
    val iconRes: Int,
    val iconName: String,
    val iconTint: Color,
    val iconColorKey: String?,
    val isFavorite: Boolean,
    val expiryEnabled: Boolean,
    val expiredAt: String?,
    val lastRecordedAt: String
)