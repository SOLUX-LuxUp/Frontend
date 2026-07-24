package com.solux.luxup.taptap.feature.insight.weekly.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.solux.luxup.taptap.core.ui.theme.parseHexColor
import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount
import com.solux.luxup.taptap.feature.insight.weekly.model.InsightDailyTapCount
import java.time.LocalDate

private val TitleColor = Color(0xFF6D6D6D)
private val LabelColor = Color(0xFF6D6D6D)
private val SubColor = Color(0xFFB0B0B0)
private val CardBorder = Color(0xFFEDEDED)
private val AxisLineColor = Color(0xFF6D6D6D)

/** 막대 영역 높이 (가장 높은 막대가 이 높이를 꽉 채움) */
private val BarAreaHeight = 140.dp

// 카테고리 색 지정이 없을 때(⚠ API 미제공 대비) 순번 기준으로 대체할 팔레트
private val CategoryPalette = listOf(
    Color(0xFF0059C5),
    Color(0xFFFFC94C),
    Color(0xFFFF6B6B),
    Color(0xFF7CCBFF),
    Color(0xFFB0B8C1),
)

/** categoryId(문자열) → 표시 색. categoryTapCounts에 색이 있으면 그 값, 없으면 팔레트 순번으로 대체 */
private fun buildCategoryColorMap(categoryTapCounts: List<InsightCategoryTapCount>): Map<String, Color> =
    categoryTapCounts
        .sortedByDescending { it.count }
        .mapIndexed { index, category ->
            category.categoryId.toString() to (category.categoryColor?.let { parseHexColor(it) } ?: CategoryPalette[index % CategoryPalette.size])
        }
        .toMap()

/** "레포트" 위클리 - 활동 기록 (요일별 카테고리 막대), 카드 없이 차트만 — 다른 섹션과 한 카드에 묶어 쓸 때 사용 */
@Composable
fun InsightWeeklyActivityBarChart(
    dailyTapCounts: List<InsightDailyTapCount>,
    categoryTapCounts: List<InsightCategoryTapCount>,
    modifier: Modifier = Modifier
) {
    val maxCount = dailyTapCounts.maxOfOrNull { it.total } ?: 0
    val colorMap = remember(categoryTapCounts) { buildCategoryColorMap(categoryTapCounts) }

    if (dailyTapCounts.isEmpty() || maxCount <= 0) {
        Text(
            "아직 이번 주 기록이 없어요",
            fontSize = 14.sp,
            color = SubColor,
            textAlign = TextAlign.Center,
            modifier = modifier.fillMaxWidth()
        )
    } else {
        Box(modifier = modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                dailyTapCounts.forEach { day ->
                    DayBar(day = day, maxCount = maxCount, colorMap = colorMap)
                }
            }
            // 막대가 끝나는 지점(x축) 기준선 — 모든 막대 영역 높이가 같아 한 줄로 정렬된다
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = BarAreaHeight)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AxisLineColor)
            )
        }
    }
}

/** "레포트" 위클리 - 활동 기록 (요일별 카테고리 막대) — 독립된 카드 (전체보기 화면 전용) */
@Composable
fun InsightWeeklyActivityBarSection(
    dailyTapCounts: List<InsightDailyTapCount>,
    categoryTapCounts: List<InsightCategoryTapCount>,
    modifier: Modifier = Modifier
) {
    SectionCard(
        modifier = modifier,
        borderColor = CardBorder,
        contentPadding = PaddingValues(18.dp)
    ) {
        Text(text = "활동 기록", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        Spacer(Modifier.height(20.dp))
        InsightWeeklyActivityBarChart(dailyTapCounts = dailyTapCounts, categoryTapCounts = categoryTapCounts)
    }
}

@Composable
private fun DayBar(
    day: InsightDailyTapCount,
    maxCount: Int,
    colorMap: Map<String, Color>
) {
    val heightRatio = (day.total.toFloat() / maxCount).coerceIn(0f, 1f)
    val animatedRatio by animateFloatAsState(
        targetValue = heightRatio,
        animationSpec = tween(durationMillis = 600),
        label = "weeklyDayBar"
    )
    val slices = remember(day) { day.categories.entries.sortedBy { it.key.toLongOrNull() ?: 0L } }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .height(BarAreaHeight)
                .width(18.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BarAreaHeight * animatedRatio)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                slices.forEach { (categoryId, count) ->
                    val weight = count.toFloat().coerceAtLeast(0.01f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight)
                            .background(colorMap[categoryId] ?: CategoryPalette.last())
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = day.date.toWeekdayLabel(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = LabelColor
        )
    }
}

/** "2026-05-04" → "월" */
private fun String.toWeekdayLabel(): String =
    try {
        when (LocalDate.parse(this).dayOfWeek.value) {
            1 -> "월"; 2 -> "화"; 3 -> "수"; 4 -> "목"
            5 -> "금"; 6 -> "토"; else -> "일"
        }
    } catch (_: Exception) {
        "-"
    }

@Preview(showBackground = true, heightDp = 300)
@Composable
private fun InsightWeeklyActivityBarSectionPreview() {
    PreviewContainer {
        InsightWeeklyActivityBarSection(
            dailyTapCounts = listOf(
                InsightDailyTapCount("2026-05-04", 10, mapOf("10" to 6, "20" to 4)),
                InsightDailyTapCount("2026-05-05", 16, mapOf("10" to 8, "20" to 6, "30" to 2)),
                InsightDailyTapCount("2026-05-06", 14, mapOf("10" to 5, "20" to 5, "30" to 4)),
                InsightDailyTapCount("2026-05-07", 11, mapOf("10" to 6, "20" to 5)),
                InsightDailyTapCount("2026-05-08", 18, mapOf("10" to 8, "20" to 7, "30" to 3)),
                InsightDailyTapCount("2026-05-09", 7, mapOf("10" to 5, "20" to 2)),
                InsightDailyTapCount("2026-05-10", 12, mapOf("10" to 6, "20" to 6)),
            ),
            categoryTapCounts = listOf(
                InsightCategoryTapCount(10L, "건강", 44, 0.5, "#4C8DFF"),
                InsightCategoryTapCount(20L, "자기계발", 35, 0.398, "#FFB84C"),
                InsightCategoryTapCount(30L, "여가", 9, 0.102, "#FF6B6B"),
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "빈 상태", heightDp = 160)
@Composable
private fun InsightWeeklyActivityBarSectionEmptyPreview() {
    PreviewContainer {
        InsightWeeklyActivityBarSection(
            dailyTapCounts = emptyList(),
            categoryTapCounts = emptyList(),
            modifier = Modifier.padding(16.dp)
        )
    }
}