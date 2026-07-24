package com.solux.luxup.taptap.feature.insight.weekly.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer

/** "< 2026년 5월 / 2주차 >" 주간 네비게이션 — [com.solux.luxup.taptap.feature.insight.daily.util.InsightDateNav] 위클리 버전 */
@Composable
fun InsightWeekNav(
    weekStart: String,
    onPrevWeek: () -> Unit = {},
    onNextWeek: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "이전 주",
            tint = Color(0xFF727272),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onPrevWeek() }
                .padding(horizontal = 30.dp)
                .size(50.dp)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = weekStart.toKoreanMonthText(),
                fontFamily = Pretendard,
                fontSize = 13.sp,
                color = Color(0xFFB0B0B0)
            )
            Text(
                text = weekStart.toWeekOfMonthText(),
                fontFamily = Pretendard,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF727272)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "다음 주",
            tint = Color(0xFF727272),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onNextWeek() }
                .padding(horizontal = 30.dp)
                .size(50.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InsightWeekNavPreview() {
    PreviewContainer {
        InsightWeekNav(weekStart = "2026-05-04", modifier = Modifier.padding(16.dp))
    }
}