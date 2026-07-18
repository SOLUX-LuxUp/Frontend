package com.solux.luxup.taptap.feature.team.presentation.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.feature.team.data.MockTeamInsightDaily
import com.solux.luxup.taptap.feature.team.presentation.insight.daily.TeamInsightDailyScreen

enum class InsightPeriod(val label: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

@Composable
fun TeamInsightScreen(
    currentUserId: Long,
    modifier: Modifier = Modifier
) {
    var period by remember { mutableStateOf(InsightPeriod.DAILY) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)      // 좌우 40 통일
    ) {
        Spacer(Modifier.padding(top = 8.dp))

        // Daily / Weekly / Monthly 토글
        InsightPeriodToggle(
            selected = period,
            onSelect = { period = it }
        )

        Spacer(Modifier.padding(top = 12.dp))

        // 날짜 네비 (< 날짜 >) — UI만, 실제 이동은 API 연결 시
        InsightDateNav(period = period)

        Spacer(Modifier.padding(top = 12.dp))

        // 기간별 내용
        when (period) {
            InsightPeriod.DAILY -> {
                TeamInsightDailyScreen(
                    data = MockTeamInsightDaily,   // ⚠ API 연결 시 ViewModel 상태로 교체
                    currentUserId = currentUserId
                )
            }
            InsightPeriod.WEEKLY -> { /* TODO: TeamInsightWeeklyScreen() */ }
            InsightPeriod.MONTHLY -> { /* TODO: TeamInsightMonthlyScreen() */ }
        }
    }
}

@Composable
private fun InsightPeriodToggle(
    selected: InsightPeriod,
    onSelect: (InsightPeriod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        InsightPeriod.entries.forEach { p ->
            val isSelected = p == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 25.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .then(
                        if (isSelected)
                            Modifier.background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF4BB4FF), Color(0xFF2085FF))
                                )
                            )
                        else
                            Modifier
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E2E2), RoundedCornerShape(24.dp))
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelect(p) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = p.label,
                    fontFamily = Pretendard,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else Color(0xFF6D6D6D)
                )
            }
        }
    }
}

@Composable
private fun InsightDateNav(period: InsightPeriod) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "‹",
            fontSize = 20.sp,
            color = Color(0xFFB0B0B0),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* TODO: 이전 날짜 */ }
                .padding(horizontal = 16.dp)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Today",
                fontFamily = Pretendard,
                fontSize = 10.sp,
                color = Color(0xFF2085FF)
            )
            Text(
                text = "2026년 5월 3일",         // 목데이터 (API 연결 시 targetDate)
                fontFamily = Pretendard,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }
        Text(
            text = "›",
            fontSize = 20.sp,
            color = Color(0xFFB0B0B0),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* TODO: 다음 날짜 */ }
                .padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 390, heightDp = 700)
@Composable
private fun TeamInsightScreenPreview() {
    TeamInsightScreen(currentUserId = 4L)
}