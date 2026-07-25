package com.solux.luxup.taptap.feature.insight.monthly.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer

/** "< 2026년 5월 >" 월간 네비게이션 — [com.solux.luxup.taptap.feature.insight.weekly.util.InsightWeekNav] 먼슬리 버전 */
@Composable
fun InsightMonthNav(
    year: Int,
    month: Int,
    onPrevMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "이전 달",
            tint = Color(0xFF727272),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onPrevMonth() }
                .padding(horizontal = 30.dp)
                .size(50.dp)
        )
        Text(
            text = monthNavLabel(year, month),
            fontFamily = Pretendard,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF727272)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "다음 달",
            tint = Color(0xFF727272),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onNextMonth() }
                .padding(horizontal = 30.dp)
                .size(50.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InsightMonthNavPreview() {
    PreviewContainer {
        InsightMonthNav(year = 2026, month = 5, modifier = Modifier.padding(16.dp))
    }
}