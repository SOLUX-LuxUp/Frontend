package com.solux.luxup.taptap.feature.insight.monthly.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightTimeSlotStat
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart
import kotlin.math.roundToInt

private val TitleColor = Color(0xFF6D6D6D)
private val HighlightGradient = Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
private val NameColor = Color(0xFF6D6D6D)
private val AxisLabelColor = Color(0xFF6D6D6D)
private val SubColor = Color(0xFFB0B0B0)
private val LineColor = Color(0xFF2085FF)
private val LegendChipBorder = Color(0xFFE2E2E2)
private val AreaGradient = Brush.verticalGradient(
    listOf(Color(0xFF2085FF), Color(0xFFFFFFFF).copy(alpha = 0f))
)

private val ChartHeight = 60.dp
private val ChartTopPadding = 28.dp // 점 위 퍼센트 라벨 공간

/** 하루를 5구간으로 나눈 고정 시간대 — API의 timeSlotCategory 키("새벽"/"아침"/...)와 매칭 */
private enum class MonthlyTimeSlot(val label: String, val hourRange: String) {
    DAWN("새벽", "00~06시"),
    MORNING("아침", "06~12시"),
    NOON("점심", "12~14시"),
    EVENING("저녁", "18~22시"),
    NIGHT("밤", "22~24시")
}

/** "시간대 별 활동 분석" — 가장 활발한 시간대 강조 문구 + 시간대별 비중 라인 차트 */
@Composable
fun InsightMonthlyTimeSlotSection(
    timeSlotCategory: Map<String, InsightTimeSlotStat>,
    categoryTapCounts: List<InsightCategoryTapCount>,
    modifier: Modifier = Modifier
) {
    val colorMap = remember(categoryTapCounts) { buildMonthlyCategoryColorMap(categoryTapCounts) }
    val slots = remember(timeSlotCategory) {
        MonthlyTimeSlot.entries.map { slot -> slot to timeSlotCategory[slot.label] }
    }
    val peak = remember(slots) { slots.maxByOrNull { (_, stat) -> stat?.ratio ?: 0.0 } }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Text(text = "시간대 별 활동 분석", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        Spacer(Modifier.height(6.dp))

        if (peak?.second == null) {
            Text(text = "아직 이번 달 기록이 없어요", fontSize = 14.sp, color = SubColor)
            return@Column
        }

        Row {
            Text(
                text = "${peak.first.label}(${peak.first.hourRange})",
                style = TextStyle(fontSize = 14.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold, brush = HighlightGradient)
            )
            Text(
                text = " 에 많이 기록했어요",
                style = TextStyle(fontSize = 14.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold, color = NameColor)
            )
        }

        Spacer(Modifier.height(20.dp))

        TimeSlotChart(slots = slots, colorMap = colorMap)

        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SubColor)
        )

        Spacer(Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            slots.forEach { (slot, _) ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = slot.hourRange.removeSuffix("시"),
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                        color = AxisLabelColor,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = slot.label,
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AxisLabelColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        TimeSlotLegend(categoryTapCounts = categoryTapCounts, colorMap = colorMap)
    }
}

/**
 * [p0], [p1], [p2] 세 점을 지나는 포물선을 이용해 x=[targetX] 지점의 y를 외삽한다 (0~[maxY]로 클램프).
 * 직선(기울기)만 이어 붙이면 데이터 점에서 곡률이 갑자기 바뀌어 꺾여 보이므로,
 * 뒤쪽 두 점의 곡률까지 반영해 자연스럽게 이어지도록 한다.
 */
private fun quadraticExtrapolate(p0: Offset, p1: Offset, p2: Offset, targetX: Float, maxY: Float): Offset {
    val x = targetX
    val y = p0.y * (x - p1.x) * (x - p2.x) / ((p0.x - p1.x) * (p0.x - p2.x)) +
        p1.y * (x - p0.x) * (x - p2.x) / ((p1.x - p0.x) * (p1.x - p2.x)) +
        p2.y * (x - p0.x) * (x - p1.x) / ((p2.x - p0.x) * (p2.x - p1.x))
    return Offset(targetX, y.coerceIn(0f, maxY))
}

@Composable
private fun TimeSlotChart(
    slots: List<Pair<MonthlyTimeSlot, InsightTimeSlotStat?>>,
    colorMap: Map<Long, Color>
) {
    val maxRatio = remember(slots) {
        slots.maxOfOrNull { (_, stat) -> stat?.ratio ?: 0.0 }?.takeIf { it > 0.0 } ?: 1.0
    }
    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 700),
        label = "monthlyTimeSlotChart"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ChartHeight + ChartTopPadding)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(ChartHeight).align(Alignment.BottomCenter)) {
            // 데이터 점들은 여백을 두고 안쪽에 배치한다
            val margin = size.width * 0.12f
            val stepX = (size.width - margin * 2) / (slots.size - 1)
            val dataPoints = slots.mapIndexed { index, (_, stat) ->
                val ratio = ((stat?.ratio ?: 0.0) / maxRatio).toFloat().coerceIn(0f, 1f)
                Offset(x = margin + index * stepX, y = size.height * (1f - ratio * progress))
            }
            // 양 끝은 0(바닥선)에 딱 붙이는 대신, 데이터 곡선의 곡률을 그대로 이어서
            // 캔버스 가장자리까지 자연스럽게 연장한다 (포물선 외삽)
            val leftEdge = quadraticExtrapolate(
                dataPoints[0], dataPoints[1], dataPoints[2],
                targetX = 0f, maxY = size.height
            )
            val rightEdge = quadraticExtrapolate(
                dataPoints[dataPoints.size - 1], dataPoints[dataPoints.size - 2], dataPoints[dataPoints.size - 3],
                targetX = size.width, maxY = size.height
            )
            val points = listOf(leftEdge) + dataPoints + listOf(rightEdge)

            // Catmull-Rom → cubic Bezier 변환으로 각 점을 지나는 부드러운 곡선을 그린다
            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points.getOrElse(i - 1) { points[i] }
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val p3 = points.getOrElse(i + 2) { points[i + 1] }
                    val control1 = Offset(p1.x + (p2.x - p0.x) / 6f, p1.y + (p2.y - p0.y) / 6f)
                    val control2 = Offset(p2.x - (p3.x - p1.x) / 6f, p2.y - (p3.y - p1.y) / 6f)
                    cubicTo(control1.x, control1.y, control2.x, control2.y, p2.x, p2.y)
                }
            }
            val areaPath = Path().apply {
                addPath(linePath)
                lineTo(points.last().x, size.height)
                lineTo(points.first().x, size.height)
                close()
            }

            drawPath(path = areaPath, brush = AreaGradient)
            drawPath(path = linePath, color = LineColor, style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round))

            slots.forEachIndexed { index, (_, stat) ->
                val color = stat?.categoryId?.let { colorMap[it] } ?: LineColor
                drawCircle(color = Color.White, radius = 7.dp.toPx(), center = dataPoints[index])
                drawCircle(color = color, radius = 5.dp.toPx(), center = dataPoints[index])
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            slots.forEach { (_, stat) ->
                val ratio = ((stat?.ratio ?: 0.0) / maxRatio).toFloat().coerceIn(0f, 1f)
                val percent = ((stat?.ratio ?: 0.0) * 100).roundToInt()

                Text(
                    text = "$percent%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = NameColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = ChartTopPadding * (1f - ratio) * 0.85f)
                )
            }
        }
    }
}

@Composable
private fun TimeSlotLegend(
    categoryTapCounts: List<InsightCategoryTapCount>,
    colorMap: Map<Long, Color>
) {
    val sorted = remember(categoryTapCounts) { categoryTapCounts.sortedByDescending { it.count } }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        sorted.forEach { category ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(15))
                    .border(1.dp, LegendChipBorder, RoundedCornerShape(15))
                    .padding(horizontal = 4.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(colorMap[category.categoryId] ?: LineColor)
                )
                Spacer(Modifier.width(4.dp))
                Text(text = category.categoryName, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = NameColor)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun InsightMonthlyTimeSlotSectionPreview() {
    PreviewContainer {
        InsightMonthlyTimeSlotSection(
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