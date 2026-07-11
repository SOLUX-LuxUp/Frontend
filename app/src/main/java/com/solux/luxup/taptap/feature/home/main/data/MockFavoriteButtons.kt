package com.solux.luxup.taptap.feature.home.main.data

import androidx.compose.ui.graphics.Color
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.feature.home.main.model.FavoriteButton

val mockFavoriteButtons = listOf(
    FavoriteButton(
        iconRes = R.drawable.bt_water_drop,
        iconTint = Color(0xFF7CCBFF),
        title = "물 마시기",
        lastRecordedAt = "2026-07-10T09:00:00Z"
    ),
    FavoriteButton(
        iconRes = R.drawable.bt_bookmark,
        iconTint = Color(0xFFFFD53F),
        title = "일기 쓰기",
        lastRecordedAt = "2026-07-01T09:00:00Z"
    ),
    FavoriteButton(
        iconRes = R.drawable.bt_dumbbell,
        iconTint = Color(0xFF0073FF),
        title = "운동 하기",
        lastRecordedAt = "2026-07-06T09:00:00Z"
    )
)