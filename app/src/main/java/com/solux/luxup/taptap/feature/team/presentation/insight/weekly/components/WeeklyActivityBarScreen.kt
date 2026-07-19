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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.solux.luxup.taptap.feature.team.model.TeamInsightBarCategory
import com.solux.luxup.taptap.feature.team.model.TeamInsightDailyBar
import java.time.LocalDate

// ⚠ 임시 색 — 마지막에 Color.kt 토큰으로 교체
private val TitleColor = Color(0xFF1A1D22)
private val LabelColor = Color(0xFF6D6D6D)
private val SubColor = Color(0xFF8A94A6)
private val CardBorder = Color(0xFFEDEDED)

/** 막대 영역 높이 (가장 높은 막대가 이 높이를 꽉 채움) */
private val BarAreaHeight = 140.dp

@Composable
fun WeeklyActivityBarSection(
    dailyTapCounts: List<TeamInsightDailyBar>,
    modifier: Modifier = Modifier
) {
    // 최대값 기준으로 막대 높이 비율 계산
    val maxCount = dailyTapCounts.maxOfOrNull { it.tapCount } ?: 0

    SectionCard(
        modifier = modifier,
        borderColor = CardBorder,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Text(
            text = "활동 기록",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TitleColor
        )
        Spacer(Modifier.height(20.dp))

        if (dailyTapCounts.isEmpty() || maxCount <= 0) {
            Text("아직 이번 주 기록이 없어요", fontSize = 14.sp, color = SubColor)
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                dailyTapCounts.forEach { day ->
                    DayBar(day = day, maxCount = maxCount)
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

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 막대 영역 — 아래 정렬로 위로 자람
        Box(
            modifier = Modifier
                .height(BarAreaHeight)
                .width(18.dp),           // ⚠ 시안 막대 폭으로 조정
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BarAreaHeight * animatedRatio)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                // 카테고리 스택 — 위에서 아래로 쌓임
                day.categories.forEach { category ->
                    val weight = category.tapCount.toFloat().coerceAtLeast(0.01f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight)
                            .background(category.categoryColor.toBarColor())
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // 요일 라벨
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

/** ⚠ 임시 hex 파서 — core 공용 생기면 교체 */
private fun String.toBarColor(): Color =
    try {
        Color(("FF" + this.removePrefix("#")).toLong(16))
    } catch (_: Exception) {
        Color(0xFFB0B8C1)
    }

@Preview(showBackground = true, heightDp = 300)
@Composable
private fun WeeklyActivityBarSectionPreview() {
    PreviewContainer {
        WeeklyActivityBarSection(
            dailyTapCounts = listOf(
                TeamInsightDailyBar("2026-05-04", 7, listOf(
                    TeamInsightBarCategory(10L, "업무", "#FFC94C", 4),
                    TeamInsightBarCategory(20L, "디자인", "#4C8DFF", 3),
                )),
                TeamInsightDailyBar("2026-05-05", 11, listOf(
                    TeamInsightBarCategory(10L, "업무", "#FFC94C", 7),
                    TeamInsightBarCategory(20L, "디자인", "#4C8DFF", 4),
                )),
                TeamInsightDailyBar("2026-05-06", 6, listOf(
                    TeamInsightBarCategory(20L, "디자인", "#4C8DFF", 4),
                    TeamInsightBarCategory(30L, "소통", "#FF6B6B", 2),
                )),
                TeamInsightDailyBar("2026-05-07", 8, listOf(
                    TeamInsightBarCategory(10L, "업무", "#FFC94C", 5),
                    TeamInsightBarCategory(30L, "소통", "#FF6B6B", 3),
                )),
                TeamInsightDailyBar("2026-05-08", 12, listOf(
                    TeamInsightBarCategory(30L, "소통", "#FF6B6B", 8),
                    TeamInsightBarCategory(10L, "업무", "#FFC94C", 4),
                )),
                TeamInsightDailyBar("2026-05-09", 5, listOf(
                    TeamInsightBarCategory(20L, "디자인", "#4C8DFF", 3),
                    TeamInsightBarCategory(10L, "업무", "#FFC94C", 2),
                )),
                TeamInsightDailyBar("2026-05-10", 7, listOf(
                    TeamInsightBarCategory(10L, "업무", "#FFC94C", 4),
                    TeamInsightBarCategory(20L, "디자인", "#4C8DFF", 3),
                )),
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "빈 상태", heightDp = 160)
@Composable
private fun WeeklyActivityBarSectionEmptyPreview() {
    PreviewContainer {
        WeeklyActivityBarSection(dailyTapCounts = emptyList(), modifier = Modifier.padding(16.dp))
    }
}