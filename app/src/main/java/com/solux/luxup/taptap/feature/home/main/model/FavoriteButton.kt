package com.solux.luxup.taptap.feature.home.main.model

import androidx.compose.ui.graphics.Color

data class FavoriteButton(
    val iconRes: Int,
    val iconTint: Color,
    val title: String,
    val lastRecordedAt: String
)