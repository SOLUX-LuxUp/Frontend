package com.solux.luxup.taptap.feature.insight.lifestyle.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.lifestyle.model.InsightLifestyleAnalysisButton
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

private val CardGradient = Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
private val ChipBg = Color.White

/** "이 달의 라이프 스타일" 요약 카드 — 라벨 + 코멘트 + 분석에 사용한 버튼 칩 목록 */
@Composable
fun InsightLifestyleSummaryCard(
    lifestyleLabel: String,
    lifestyleCaption: String,
    analysisButtons: List<InsightLifestyleAnalysisButton>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardGradient)
            .padding(20.dp)
    ) {
        Text(text = "이 달의 라이프 스타일", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE2E2E2))
        Spacer(Modifier.height(12.dp))
        Text(text = lifestyleLabel, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(6.dp))
        Text(text = lifestyleCaption, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)

        if (analysisButtons.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            Text(text = "분석에 사용한 버튼", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
            Spacer(Modifier.height(6.dp))
            WrapRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = 8.dp,
                verticalSpacing = 8.dp
            ) {
                analysisButtons.forEach { button ->
                    AnalysisButtonChip(button)
                }
            }
        }
    }
}

/** 줄바꿈이 필요하면 다음 줄로 넘기는 가로 배치 — 넘칠 때 스크롤 대신 아래로 wrap */
@Composable
private fun WrapRow(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val hSpacingPx = horizontalSpacing.roundToPx()
        val vSpacingPx = verticalSpacing.roundToPx()
        val maxWidth = constraints.maxWidth

        val placeables = measurables.map { it.measure(Constraints(maxWidth = maxWidth)) }

        val rows = mutableListOf<MutableList<Placeable>>()
        val rowWidths = mutableListOf<Int>()
        val rowHeights = mutableListOf<Int>()

        placeables.forEach { placeable ->
            val lastIndex = rows.lastIndex
            val fitsInLastRow = lastIndex >= 0 &&
                rowWidths[lastIndex] + hSpacingPx + placeable.width <= maxWidth
            if (fitsInLastRow) {
                rows[lastIndex].add(placeable)
                rowWidths[lastIndex] += hSpacingPx + placeable.width
                rowHeights[lastIndex] = maxOf(rowHeights[lastIndex], placeable.height)
            } else {
                rows.add(mutableListOf(placeable))
                rowWidths.add(placeable.width)
                rowHeights.add(placeable.height)
            }
        }

        val totalHeight = rowHeights.sum() + vSpacingPx * (rows.size - 1).coerceAtLeast(0)

        layout(maxWidth, totalHeight) {
            var y = 0
            rows.forEachIndexed { index, row ->
                var x = 0
                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + hSpacingPx
                }
                y += rowHeights[index] + vSpacingPx
            }
        }
    }
}

@Composable
private fun AnalysisButtonChip(button: InsightLifestyleAnalysisButton, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(ChipBg)
            .padding(start = 6.dp, end = 10.dp, top = 0.dp, bottom = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(ButtonIcons.resOf(button.iconName)),
            contentDescription = null,
            tint = button.iconColor?.let { IconColor.from(it).color } ?: Color.White,
            modifier = Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(2.dp)
        )
        Spacer(Modifier.width(2.dp))
        Text(text = button.buttonName, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF6D6D6D))
    }
}

@Preview(showBackground = true)
@Composable
private fun InsightLifestyleSummaryCardPreview() {
    PreviewContainer {
        InsightLifestyleSummaryCard(
            lifestyleLabel = "규칙적인 기억",
            lifestyleCaption = "규칙적으로 기록하며 리듬을 만들어가고 있어요.",
            analysisButtons = listOf(
                InsightLifestyleAnalysisButton(1L, "물 마시기", "drink", "#4D96FF"),
                InsightLifestyleAnalysisButton(2L, "약 먹기", "medicine", "#FF6B6B"),
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}