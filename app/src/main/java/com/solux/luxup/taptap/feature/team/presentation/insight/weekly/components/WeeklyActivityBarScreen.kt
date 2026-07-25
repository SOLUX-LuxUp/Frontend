package com.solux.luxup.taptap.feature.team.presentation.insight.weekly.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.ui.theme.parseHexColor
import com.solux.luxup.taptap.feature.team.model.TeamInsightBarCategory
import com.solux.luxup.taptap.feature.team.model.TeamInsightDailyBar
import java.time.LocalDate

// ⚠ 임시 색 — 마지막에 Color.kt 토큰으로 교체
private val TitleColor = Color(0xFF1A1D22)
private val ActivityLabelColor = Color(0xFF6D6D6D)  // 활동 기록 라벨 / 실선
private val LegendLabelColor = Color(0xFFB0B0B0)    // 범례 라벨
private val WeekdayColor = Color(0xFF6D6D6D)
private val SubColor = Color(0xFF8A94A6)
private val CardBorder = Color(0xFFEDEDED)
private val BaselineColor = Color(0xFF6D6D6D)       // 막대 아래 실선

private val BarAreaHeight = 140.dp
private val BarWidth = 18.dp   // ⚠ 시안 막대 폭

private data class LegendCategory(
    val categoryId: Long,
    val categoryName: String,
    val categoryColor: String
)

@Composable
fun WeeklyActivityBarSection(
    dailyTapCounts: List<TeamInsightDailyBar>,
    modifier: Modifier = Modifier
) {
    val maxCount = dailyTapCounts.maxOfOrNull { it.tapCount } ?: 0

    val legend = dailyTapCounts
        .flatMap { it.categories }
        .distinctBy { it.categoryId }
        .map { LegendCategory(it.categoryId, it.categoryName, it.categoryColor) }

    SectionCard(
        modifier = modifier,
        borderColor = CardBorder,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Text(
            text = "활동 기록",
            fontSize = 14.sp,                  // 시안 14
            fontWeight = FontWeight.Medium,    // 시안 Medium
            color = ActivityLabelColor         // #6D6D6D
        )
        Spacer(Modifier.height(20.dp))

        if (dailyTapCounts.isEmpty() || maxCount <= 0) {
            Text("아직 이번 주 기록이 없어요", fontSize = 14.sp, color = SubColor)
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top   // 범례를 위쪽 정렬
            ) {
                // 왼쪽: 막대 그래프 + 실선 + 요일
                Column(modifier = Modifier.weight(1f)) {
                    // 막대들 (실선 위에 서 있음)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        dailyTapCounts.forEach { day ->
                            DayBar(day = day, maxCount = maxCount)
                        }
                    }

                    // 검정 실선 (막대 바로 아래)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(BaselineColor)
                    )

                    Spacer(Modifier.height(8.dp))

                    // 요일 라벨 (실선 아래)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        dailyTapCounts.forEach { day ->
                            Box(
                                modifier = Modifier.width(BarWidth),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.date.toWeekdayLabel(),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = WeekdayColor
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))

                // 오른쪽: 범례 (위쪽부터 쌓임)
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    legend.forEach { item ->
                        LegendRow(name = item.categoryName, colorHex = item.categoryColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun DayBar(
    day: TeamInsightDailyBar,
    maxCount: Int
) {
    val heightRatio = (day.tapCount.toFloat() / maxCount).coerceIn(0f, 1f)
    val animatedRatio by animateFloatAsState(
        targetValue = heightRatio,
        animationSpec = tween(durationMillis = 600),
        label = "dayBar"
    )

    Box(
        modifier = Modifier
            .height(BarAreaHeight)
            .width(BarWidth),
        contentAlignment = Alignment.BottomCenter
    ) {
        // 카테고리 스택 — 각진 사각형 (clip 없음)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(BarAreaHeight * animatedRatio)
        ) {
            day.categories.forEach { category ->
                val weight = category.tapCount.toFloat().coerceAtLeast(0.01f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight)
                        .background(parseHexColor(category.categoryColor))
                )
            }
        }
    }
}

@Composable
private fun LegendRow(name: String, colorHex: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(parseHexColor(colorHex))
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = name,
            fontSize = 13.sp,                 // 시안 13
            fontWeight = FontWeight.Medium,   // 시안 Medium
            color = LegendLabelColor          // #B0B0B0
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

@Preview(showBackground = true, widthDp = 390, heightDp = 300)
@Composable
private fun WeeklyActivityBarSectionPreview() {
    PreviewContainer {
        WeeklyActivityBarSection(
            dailyTapCounts = listOf(
                TeamInsightDailyBar("2026-05-04", 7, listOf(
                    TeamInsightBarCategory(10L, "건강", "#FFC94C", 4),
                    TeamInsightBarCategory(20L, "자기계발", "#4C8DFF", 3),
                )),
                TeamInsightDailyBar("2026-05-05", 11, listOf(
                    TeamInsightBarCategory(10L, "건강", "#FFC94C", 11),
                )),
                TeamInsightDailyBar("2026-05-06", 6, listOf(
                    TeamInsightBarCategory(20L, "자기계발", "#4C8DFF", 3),
                    TeamInsightBarCategory(30L, "소통", "#FF6B6B", 3),
                )),
                TeamInsightDailyBar("2026-05-07", 8, listOf(
                    TeamInsightBarCategory(10L, "건강", "#FFC94C", 5),
                    TeamInsightBarCategory(30L, "소통", "#FF6B6B", 3),
                )),
                TeamInsightDailyBar("2026-05-08", 12, listOf(
                    TeamInsightBarCategory(30L, "소통", "#FF6B6B", 12),
                )),
                TeamInsightDailyBar("2026-05-09", 5, listOf(
                    TeamInsightBarCategory(20L, "자기계발", "#4C8DFF", 3),
                    TeamInsightBarCategory(10L, "건강", "#FFC94C", 2),
                )),
                TeamInsightDailyBar("2026-05-10", 7, listOf(
                    TeamInsightBarCategory(10L, "건강", "#FFC94C", 7),
                )),
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "빈 상태", widthDp = 390, heightDp = 160)
@Composable
private fun WeeklyActivityBarSectionEmptyPreview() {
    PreviewContainer {
        WeeklyActivityBarSection(dailyTapCounts = emptyList(), modifier = Modifier.padding(16.dp))
    }
}