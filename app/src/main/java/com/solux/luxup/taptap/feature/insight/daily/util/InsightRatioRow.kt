package com.solux.luxup.taptap.feature.insight.daily.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.daily.model.InsightButtonTapCount
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

private val NameColor = Color(0xFF6D6D6D)
private val CountColor = Color(0xFF6D6D6D)
private val TrackColor = Color(0xFFDADADA)
private val LineColor = Color(0xFF6D6D6D)
private val BarGradient = Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
private val BadgeSize = 50.dp
private val LineGap = 5.dp

/** 기록비율 막대 한 행 — 미리보기(레포트 메인)와 전체보기 화면이 공유 */
@Composable
fun InsightRatioRow(
    item: InsightButtonTapCount,
    denominator: Int,
    modifier: Modifier = Modifier,
    isFirst: Boolean = true,
    isLast: Boolean = true
) {
    val ratio = (item.count.toFloat() / denominator).coerceIn(0f, 1f)
    val animatedRatio by animateFloatAsState(
        targetValue = ratio,
        animationSpec = tween(durationMillis = 600),
        label = "insightRatioBar"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(BadgeSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxHeight()) {
                val cx = size.width / 2
                val center = size.height / 2
                val badgeRadius = BadgeSize.toPx() / 2
                val gap = LineGap.toPx()
                val strokeWidth = 0.5.dp.toPx()
                if (!isFirst) {
                    drawLine(
                        color = LineColor,
                        start = Offset(cx, 0f),
                        end = Offset(cx, (center - badgeRadius - gap).coerceAtLeast(0f)),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
                if (!isLast) {
                    drawLine(
                        color = LineColor,
                        start = Offset(cx, (center + badgeRadius + gap).coerceAtMost(size.height)),
                        end = Offset(cx, size.height),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
            InsightIconBadge(
                buttonName = item.buttonName,
                iconName = item.iconName,
                iconColor = item.iconColor,
                size = BadgeSize
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = item.buttonName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NameColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .width(60.dp)
                .padding(vertical = 25.dp)
        )

        Spacer(Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(TrackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedRatio)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(BarGradient)
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = "${item.count}회",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CountColor,
            textAlign = TextAlign.End,
            modifier = Modifier.width(30.dp)
        )
    }
}

/** ALL▾ 필터 — null이면 전체, ""이면 카테고리 없는 항목만("No Category" 선택 시), 그 외엔 categoryName 일치 항목만 */
fun List<InsightButtonTapCount>.filterByCategory(categoryName: String?): List<InsightButtonTapCount> =
    this
        .filter { categoryName == null || it.categoryName == categoryName }
        .sortedByDescending { it.count }

private val PreviewRatioItems = listOf(
    InsightButtonTapCount(
        buttonId = 1L, buttonName = "물 마시기", count = 19, ratio = 19 / 55.0,
        iconName = "drink", iconColor = "#4C9AFF", categoryId = 10L, categoryName = "건강"
    ),
    InsightButtonTapCount(
        buttonId = 2L, buttonName = "경제공부", count = 17, ratio = 17 / 55.0,
        iconName = "book", iconColor = "#4C8DFF", categoryId = 20L, categoryName = "자기계발"
    ),
    InsightButtonTapCount(
        buttonId = 5L, buttonName = "일기쓰기", count = 1, ratio = 1 / 55.0,
        iconName = "note", iconColor = "#FFC107", categoryId = 20L, categoryName = "자기계발"
    )
)

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun InsightRatioRowPreview() {
    PreviewContainer {
        Column(modifier = Modifier.padding(18.dp)) {
            PreviewRatioItems.forEachIndexed { index, item ->
                InsightRatioRow(
                    item = item,
                    denominator = 55,
                    isFirst = index == 0,
                    isLast = index == PreviewRatioItems.lastIndex
                )
            }
        }
    }
}