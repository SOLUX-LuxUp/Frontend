package com.solux.luxup.taptap.feature.insight.daily.util

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.daily.model.InsightTimelineItem
import java.time.LocalDateTime

private val TitleColor = Color(0xFF6D6D6D)
private val SubColor = Color(0xFF6D6D6D)
private val LineColor = Color(0xFF6D6D6D)
private val BadgeSize = 50.dp
private val LineGap = 5.dp

/** 타임라인 한 행 — 미리보기(레포트 메인)와 전체보기 화면이 공유 */
@Composable
fun InsightTimelineRow(
    item: InsightTimelineItem,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit = {}
) {
    val now = rememberTickingNow()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
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

        Spacer(Modifier.width(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 18.dp)
        ) {
            Text(
                text = item.buttonName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TitleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = "${item.recordedAt.toClockText()} • ${item.recordedAt.toElapsedText(now)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SubColor,
                maxLines = 1
            )
        }

        trailingContent()
    }
}

private val PreviewTimeline = listOf(
    InsightTimelineItem(
        recordId = 1L, buttonId = 1L, buttonName = "물 마시기",
        recordedAt = LocalDateTime.now().minusHours(3).toString(),
        iconName = "drink", iconColor = "#4C9AFF"
    ),
    InsightTimelineItem(
        recordId = 2L, buttonId = 2L, buttonName = "경제공부",
        recordedAt = LocalDateTime.now().minusHours(5).toString(),
        iconName = "book", iconColor = "#4C8DFF"
    ),
    InsightTimelineItem(
        recordId = 3L, buttonId = 3L, buttonName = "필기하기",
        recordedAt = LocalDateTime.now().minusHours(9).toString(),
        iconName = "pencil", iconColor = "#FF5C5C"
    )
)

@Preview(showBackground = true, heightDp = 320)
@Composable
private fun InsightTimelineRowPreview() {
    PreviewContainer {
        Column(modifier = Modifier.padding(18.dp)) {
            PreviewTimeline.forEachIndexed { index, item ->
                InsightTimelineRow(
                    item = item,
                    isFirst = index == 0,
                    isLast = index == PreviewTimeline.lastIndex
                )
            }
        }
    }
}

/** 전체보기 화면처럼 "..." 트레일링 메뉴가 붙은 형태 */
@Preview(showBackground = true, name = "trailing 메뉴 포함", heightDp = 320)
@Composable
private fun InsightTimelineRowWithTrailingPreview() {
    PreviewContainer {
        Column(modifier = Modifier.padding(16.dp)) {
            PreviewTimeline.forEachIndexed { index, item ->
                InsightTimelineRow(
                    item = item,
                    isFirst = index == 0,
                    isLast = index == PreviewTimeline.lastIndex,
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_more),
                            contentDescription = "더보기",
                            tint = SubColor,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(20.dp)
                        )
                    }
                )
            }
        }
    }
}