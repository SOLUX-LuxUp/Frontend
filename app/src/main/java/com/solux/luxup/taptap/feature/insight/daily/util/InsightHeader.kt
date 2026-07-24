package com.solux.luxup.taptap.feature.insight.daily.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.BackArrowIcon
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

private enum class InsightPeriod(val label: String) {
    DAILY("Daily"), WEEKLY("Weekly"), MONTHLY("Monthly")
}

/** "레포트" 타이틀 + Daily/Weekly/Monthly 토글 (⚠ Weekly/Monthly 미구현 — 탭만 존재) */
@Composable
fun InsightReportTitle(
    onSelectWeekly: () -> Unit = {},
    onSelectMonthly: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "레포트",
            fontFamily = Pretendard,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InsightPeriod.entries.forEach { period ->
                val isSelected = period == InsightPeriod.DAILY
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (isSelected) {
                                Modifier.background(
                                    Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd)),
                                    RoundedCornerShape(26.dp)
                                )
                            } else {
                                Modifier
                                    .background(Color.White, RoundedCornerShape(26.dp))
                                    .border(1.dp, Color(0xFFB1B1B1), RoundedCornerShape(26.dp))
                            }
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            when (period) {
                                InsightPeriod.WEEKLY -> onSelectWeekly()
                                InsightPeriod.MONTHLY -> onSelectMonthly()
                                InsightPeriod.DAILY -> {}
                            }
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = period.label,
                        fontFamily = Pretendard,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) Color.White else Color(0xFF727272)
                    )
                }
            }
        }
    }
}

/** "< Today  2026년 5월 3일  >" 날짜 네비게이션 */
@Composable
fun InsightDateNav(
    targetDate: String,
    onPrevDay: () -> Unit = {},
    onNextDay: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "이전 날짜",
            tint = Color(0xFF727272),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onPrevDay() }
                .padding(horizontal = 30.dp)
                .size(50.dp)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Today", fontFamily = Pretendard, fontSize = 13.sp, color = Color(0xFFB0B0B0))
            Text(
                text = targetDate.toKoreanDateText(),
                fontFamily = Pretendard,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF727272)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "다음 날짜",
            tint = Color(0xFF727272),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onNextDay() }
                .padding(horizontal = 30.dp)
                .size(50.dp)
        )
    }
}

/** 전체보기 화면 상단 — 뒤로가기 + 타이틀 */
@Composable
fun InsightBackHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        BackArrowIcon(
            tint = Color(0xFFB1B1B1),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(onClick = onBack)
                .padding(4.dp)
                .size(30.dp)
        )
        Text(
            text = title,
            fontFamily = Pretendard,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}