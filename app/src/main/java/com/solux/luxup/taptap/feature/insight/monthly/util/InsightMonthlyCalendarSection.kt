package com.solux.luxup.taptap.feature.insight.monthly.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import java.time.LocalDate
import java.time.YearMonth

private val TitleColor = Color(0xFF6D6D6D)
private val LabelColor = Color(0xFF6D6D6D)
private val DayTextColor = Color(0xFF6D6D6D)
private val CardBorder = Color(0xFFEDEDED)

private val WeekdayLabels = listOf("일", "월", "화", "수", "목", "금", "토")

/** 캘린더 히트맵 색 단계 — 범례 고정 임계값 (개인 먼슬리 전용, [com.solux.luxup.taptap.feature.team.model.HeatLevel]과 동일 임계값) */
private enum class MonthlyHeatLevel(val label: String) {
    NONE("기록 없음"),
    LV1("10회 미만"),
    LV2("10회 이상\n20회 미만"),
    LV3("20회 이상\n30회 미만"),
    LV4("30회 이상\n40회 미만"),
    LV5("40회 이상");

    companion object {
        fun of(tapCount: Int): MonthlyHeatLevel = when {
            tapCount <= 0 -> NONE
            tapCount < 10 -> LV1
            tapCount < 20 -> LV2
            tapCount < 30 -> LV3
            tapCount < 40 -> LV4
            else -> LV5
        }
    }
}

private fun MonthlyHeatLevel.bgColor(): Color = when (this) {
    MonthlyHeatLevel.NONE -> Color.Transparent
    MonthlyHeatLevel.LV1 -> Color(0xFFCCEBFF)
    MonthlyHeatLevel.LV2 -> Color(0xFF99D8FF)
    MonthlyHeatLevel.LV3 -> Color(0xFF66C4FF)
    MonthlyHeatLevel.LV4 -> Color(0xFF33B1FF)
    MonthlyHeatLevel.LV5 -> Color(0xFF009DFF)
}

private fun MonthlyHeatLevel.textColor(): Color = when (this) {
    MonthlyHeatLevel.LV4, MonthlyHeatLevel.LV5 -> Color.White
    else -> DayTextColor
}

/** "캘린더" — 일자별 기록 수를 히트맵 색으로 표시 + 하단 범례 */
@Composable
fun InsightMonthlyCalendarSection(
    year: Int,
    month: Int,
    dailyTapCounts: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val yearMonth = remember(year, month) { YearMonth.of(year, month) }
    val daysInMonth = yearMonth.lengthOfMonth()
    // 1일의 요일 → 앞쪽 빈 칸 수 (일요일 시작 기준: 일=0 ... 토=6)
    val leadingBlanks = yearMonth.atDay(1).dayOfWeek.value % 7

    val cells: List<Int?> = List(leadingBlanks) { null } + (1..daysInMonth).toList()
    val weeks = cells.chunked(7)

    SectionCard(
        modifier = modifier,
        borderColor = CardBorder,
        contentPadding = PaddingValues(18.dp)
    ) {
        Text(text = "캘린더", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        Spacer(Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            WeekdayLabels.forEach { label ->
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LabelColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                            DayCell(day = day, tapCount = dailyTapCounts[dateKey] ?: 0)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        HeatLegend()
    }
}

@Composable
private fun DayCell(day: Int, tapCount: Int) {
    val level = MonthlyHeatLevel.of(tapCount)

    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(level.bgColor()),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$day", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = level.textColor())
    }
}

@Composable
private fun HeatLegend() {
    val levels = listOf(
        MonthlyHeatLevel.LV1,
        MonthlyHeatLevel.LV2,
        MonthlyHeatLevel.LV3,
        MonthlyHeatLevel.LV4,
        MonthlyHeatLevel.LV5
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(0.dp))
    ) {
        levels.forEach { level ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(level.bgColor())
                    .padding(vertical = 8.dp),
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
private fun InsightMonthlyCalendarSectionPreview() {
    PreviewContainer {
        InsightMonthlyCalendarSection(
            year = 2026,
            month = 5,
            dailyTapCounts = mapOf(
                "2026-05-01" to 8,
                "2026-05-04" to 15,
                "2026-05-05" to 42,
                "2026-05-06" to 12,
                "2026-05-09" to 38,
                "2026-05-10" to 6,
                "2026-05-12" to 45,
                "2026-05-20" to 25,
                "2026-05-21" to 9,
                "2026-05-25" to 18,
                "2026-05-31" to 5,
            ),
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}