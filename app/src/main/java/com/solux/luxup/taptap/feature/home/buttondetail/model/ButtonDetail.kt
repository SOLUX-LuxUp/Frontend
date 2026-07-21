package com.solux.luxup.taptap.feature.home.buttondetail.model

import androidx.compose.ui.graphics.Color

data class ButtonDetail(
    val buttonId: Long,
    val title: String,
    val category: String,
    val iconRes: Int,
    val iconTint: Color
)