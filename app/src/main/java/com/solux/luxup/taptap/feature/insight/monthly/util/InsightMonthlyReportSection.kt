package com.solux.luxup.taptap.feature.insight.monthly.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthlyRankedButton
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthlyTopCategory
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightTimeSlotStat

private val TitleColor = Color(0xFF6D6D6D)
private val CardBg = Color(0xFFDEEFFF)
private val CardCornerRadius = 14.dp

/**
 * "월간 기록 레포트" (요약) — TOP3/카테고리/가장 많이 기록한 날 + 평일·주말 비율 + 시간대별 활동 분석을
 * 하나의 옅은 하늘색 카드 안에 묶어서 보여준다. (지난달 대비 비교 카드와 달리 배경색이 있는 전용 래퍼)
 */
@Composable
fun InsightMonthlyReportSection(
    top3Buttons: List<InsightMonthlyRankedButton>,
    topCategory: InsightMonthlyTopCategory?,
    busiestDay: String?,
    weekdayRatio: Double,
    weekendRatio: Double,
    timeSlotCategory: Map<String, InsightTimeSlotStat>,
    categoryTapCounts: List<InsightCategoryTapCount>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CardCornerRadius))
            .background(CardBg)
            .padding(18.dp)
    ) {
        Text(text = "월간 기록 레포트", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        Spacer(Modifier.height(20.dp))

        InsightMonthlySummaryCardsRow(
            top3Buttons = top3Buttons,
            topCategory = topCategory,
            busiestDay = busiestDay,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        InsightWeekdayWeekendSection(
            weekdayRatio = weekdayRatio,
            weekendRatio = weekendRatio,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        InsightMonthlyTimeSlotSection(
            timeSlotCategory = timeSlotCategory,
            categoryTapCounts = categoryTapCounts,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun InsightMonthlyReportSectionPreview() {
    PreviewContainer {
        InsightMonthlyReportSection(
            top3Buttons = listOf(
                InsightMonthlyRankedButton(1, 1L, "물마시기", 87),
                InsightMonthlyRankedButton(2, 2L, "설거지 하기", 54),
                InsightMonthlyRankedButton(3, 3L, "러닝", 19),
            ),
            topCategory = InsightMonthlyTopCategory(1L, "자기관리", 131),
            busiestDay = "2026-05-12",
            weekdayRatio = 0.61,
            weekendRatio = 0.39,
            timeSlotCategory = mapOf(
                "새벽" to InsightTimeSlotStat(9, 0.09, 1L, "자기관리"),
                "아침" to InsightTimeSlotStat(28, 0.28, 4L, "기억"),
                "점심" to InsightTimeSlotStat(16, 0.16, 2L, "운동"),
                "저녁" to InsightTimeSlotStat(32, 0.32, 3L, "공부"),
                "밤" to InsightTimeSlotStat(13, 0.13, 5L, "기타"),
            ),
            categoryTapCounts = listOf(
                InsightCategoryTapCount(1L, "자기관리", 93, 93 / 207.0),
                InsightCategoryTapCount(2L, "운동", 58, 58 / 207.0),
                InsightCategoryTapCount(3L, "공부", 31, 31 / 207.0),
                InsightCategoryTapCount(4L, "기억", 14, 14 / 207.0),
                InsightCategoryTapCount(5L, "기타", 11, 11 / 207.0),
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}