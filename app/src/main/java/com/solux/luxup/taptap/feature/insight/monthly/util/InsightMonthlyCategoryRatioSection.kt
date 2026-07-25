package com.solux.luxup.taptap.feature.insight.monthly.util

import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount
import kotlin.math.roundToInt

private val TitleColor = Color(0xFF6D6D6D)
private val NameColor = Color(0xFF6D6D6D)
private val CountBlue = Color(0xFF0099FF)
private val CardBorder = Color(0xFFEDEDED)
private val SubColor = Color(0xFFB0B0B0)

/** "카테고리 비율" — 도넛 + (색·이름·퍼센트) 범례 */
@Composable
fun InsightMonthlyCategoryRatioSection(
    categoryTapCounts: List<InsightCategoryTapCount>,
    modifier: Modifier = Modifier
) {
    SectionCard(
        modifier = modifier,
        borderColor = CardBorder,
        contentPadding = PaddingValues(18.dp)
    ) {
        Text(text = "카테고리 비율", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        Spacer(Modifier.height(18.dp))

        val sorted = remember(categoryTapCounts) { categoryTapCounts.sortedByDescending { it.count } }
        val total = sorted.sumOf { it.count }
        val colorMap = remember(categoryTapCounts) { buildMonthlyCategoryColorMap(categoryTapCounts) }

        if (sorted.isEmpty() || total <= 0) {
            Text("아직 이번 달 기록이 없어요", fontSize = 14.sp, color = SubColor)
            return@SectionCard
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryRatioDonut(
                sorted = sorted,
                total = total,
                colorMap = colorMap,
                diameter = 130.dp,
                ringWidth = 30.dp
            )

            Spacer(Modifier.width(30.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sorted.forEach { category ->
                    CategoryLegendRow(
                        category = category,
                        color = colorMap[category.categoryId] ?: CountBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryRatioDonut(
    sorted: List<InsightCategoryTapCount>,
    total: Int,
    colorMap: Map<Long, Color>,
    diameter: androidx.compose.ui.unit.Dp,
    ringWidth: androidx.compose.ui.unit.Dp
) {
    Box(modifier = Modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(diameter)) {
            val strokePx = ringWidth.toPx()
            val inset = strokePx / 2
            val arcSize = Size(size.width - strokePx, size.height - strokePx)
            val topLeft = Offset(inset, inset)

            var startAngle = -90f
            sorted.forEach { category ->
                val sweep = category.count.toFloat() / total * 360f
                drawArc(
                    color = colorMap[category.categoryId] ?: Color.Gray,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Butt)
                )
                startAngle += sweep
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "총", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NameColor)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = "$total", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CountBlue)
                Text(text = "회", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NameColor)
            }
        }
    }
}

@Composable
private fun CategoryLegendRow(category: InsightCategoryTapCount, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = category.categoryName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NameColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${(category.ratio * 100).roundToInt()}%",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NameColor
        )
    }
}

@Preview(showBackground = true, heightDp = 300)
@Composable
private fun InsightMonthlyCategoryRatioSectionPreview() {
    PreviewContainer {
        InsightMonthlyCategoryRatioSection(
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