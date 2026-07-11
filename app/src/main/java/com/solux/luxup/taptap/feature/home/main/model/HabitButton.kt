package com.solux.luxup.taptap.feature.home.main.model

import androidx.compose.ui.graphics.Color

data class HabitButton(
    val title: String,
    val category: String,
    val iconRes: Int,
    val iconTint: Color,
    val isFavorite: Boolean,
    val lastRecordedAt: String
)