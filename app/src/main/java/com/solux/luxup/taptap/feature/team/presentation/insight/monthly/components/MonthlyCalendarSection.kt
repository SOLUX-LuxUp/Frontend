package com.solux.luxup.taptap.feature.team.presentation.insight.monthly.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.model.HeatLevel
import com.solux.luxup.taptap.feature.team.model.TeamInsightCalendarDay
import java.time.LocalDate
import java.time.YearMonth

// ⚠ 임시 색 — 마지막에 Color.kt 토큰으로 교체
private val TitleColor = Color(0xFF6D6D6D)
private val LabelColor = Color(0xFF6D6D6D)
private val DayTextColor = Color(0xFF6D6D6D)
private val CardBorder = Color(0xFFEDEDED)

private val WeekdayLabels = listOf("일", "월", "화", "수", "목", "금", "토")

/** 히트맵 단계별 배경색 (연한 → 진한) */
private fun HeatLevel.bgColor(): Color = when (this) {
    HeatLevel.NONE -> Color.Transparent
    HeatLevel.LV1 -> Color(0xFFCCEBFF)
    HeatLevel.LV2 -> Color(0xFF99D8FF)
    HeatLevel.LV3 -> Color(0xFF66C4FF)
    HeatLevel.LV4 -> Color(0xFF33B1FF)
    HeatLevel.LV5 -> Color(0xFF009DFF)
}

/** 단계별 글자색 (진한 배경엔 흰 글자) */
private fun HeatLevel.textColor(): Color = when (this) {
    HeatLevel.LV4, HeatLevel.LV5 -> Color.White
    else -> DayTextColor
}

@Composable
fun MonthlyCalendarSection(
    year: Int,
    month: Int,
    dailyTapCounts: List<TeamInsightCalendarDay>,
    modifier: Modifier = Modifier
) {
    // 날짜 → 기록 수 맵 (없는 날은 0)
    val countByDay = remember(dailyTapCounts) {
        dailyTapCounts.associate { it.date to it.tapCount }
    }

    val yearMonth = YearMonth.of(year, month)
    val daysInMonth = yearMonth.lengthOfMonth()
    // 1일의 요일 → 앞쪽 빈 칸 수 (일요일 시작 기준: 일=0 ... 토=6)
    val leadingBlanks = yearMonth.atDay(1).dayOfWeek.value % 7

    // 빈 칸 + 날짜를 7개씩 주 단위로 자름
    val cells: List<Int?> = List(leadingBlanks) { null } + (1..daysInMonth).toList()
    val weeks = cells.chunked(7)

    SectionCard(
        modifier = modifier,
        borderColor = CardBorder,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Text(
            text = "캘린더",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TitleColor
        )
        Spacer(Modifier.height(22.dp))

        // 요일 헤더
        Row(modifier = Modifier.fillMaxWidth()) {
            WeekdayLabels.forEach { label ->
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LabelColor,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // 날짜 그리드
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 마지막 주가 7칸이 안 되면 뒤를 빈 칸으로 채움
                val padded = week + List(7 - week.size) { null }
                padded.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day != null) {
                            val dateKey = LocalDate.of(year, month, day).toString()
                            DayCell(day = day, tapCount = countByDay[dateKey] ?: 0)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        HeatLegend()
    }
}

@Composable
private fun DayCell(day: Int, tapCount: Int) {
    val level = HeatLevel.of(tapCount)

    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(level.bgColor()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$day",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = level.textColor()
        )
    }
}

/** 범례 — 시안 고정 임계값 5단계 (시안: 각 칸 60×43) */
@Composable
private fun HeatLegend() {
    val levels = listOf(HeatLevel.LV1, HeatLevel.LV2, HeatLevel.LV3, HeatLevel.LV4, HeatLevel.LV5)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)      // height(43.dp) 대신
            .clip(RoundedCornerShape(4.dp))
    ) {
        levels.forEach { level ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(level.bgColor())
                    .padding(vertical = 8.dp),   // 위아래 여백
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level.label,
                    fontSize = 11.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = level.textColor(),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.padding(horizontal = 3.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 460)
@Composable
private fun MonthlyCalendarSectionPreview() {
    PreviewContainer {
        MonthlyCalendarSection(
            year = 2026,
            month = 5,
            dailyTapCounts = listOf(
                TeamInsightCalendarDay("2026-05-01", 5),
                TeamInsightCalendarDay("2026-05-04", 12),
                TeamInsightCalendarDay("2026-05-05", 25),
                TeamInsightCalendarDay("2026-05-06", 33),
                TeamInsightCalendarDay("2026-05-09", 45),
                TeamInsightCalendarDay("2026-05-10", 42),
                TeamInsightCalendarDay("2026-05-20", 22),
                TeamInsightCalendarDay("2026-05-21", 35),
                TeamInsightCalendarDay("2026-05-22", 48),
                TeamInsightCalendarDay("2026-05-25", 15),
                TeamInsightCalendarDay("2026-05-31", 8),
            ),
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}

@Preview(showBackground = true, name = "빈 상태", heightDp = 440)
@Composable
private fun MonthlyCalendarSectionEmptyPreview() {
    PreviewContainer {
        MonthlyCalendarSection(
            year = 2026,
            month = 5,
            dailyTapCounts = emptyList(),
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}