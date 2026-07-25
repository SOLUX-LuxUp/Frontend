package com.solux.luxup.taptap.feature.team.presentation.insight.daily.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.feature.team.model.TeamInsightMember
import com.solux.luxup.taptap.feature.team.model.TeamInsightTimelineItem
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.solux.luxup.taptap.core.ui.theme.IconColor
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons

// ⚠ 임시 색 — 마지막에 Color.kt 토큰으로 교체
private val TitleColor = Color(0xFF6D6D6D)
private val NameColor = Color(0xFF6D6D6D)
private val SubColor = Color(0xFF6D6D6D)
private val CardBorder = Color(0xFFEDEDED)
private val IconCircleBorder = Color(0xFFECEEF1)
private val DashColor = Color(0xFFD4D9E0)

private val TimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)

@Composable
fun DailyTimelineSection(
    timeline: List<TeamInsightTimelineItem>,
    modifier: Modifier = Modifier
) {
    SectionCard(
        modifier = modifier,
        borderColor = CardBorder,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Text(
            text = "타임라인",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TitleColor
        )
        Spacer(Modifier.height(16.dp))

        if (timeline.isEmpty()) {
            Text("아직 오늘 기록이 없어요", fontSize = 14.sp, color = SubColor)
        } else {
            // 최신순 정렬 (API가 정렬해 주면 이 줄 제거 가능)
            val sorted = timeline.sortedByDescending { it.tappedAt }
            sorted.forEachIndexed { index, item ->
                TimelineRow(
                    item = item,
                    isFirst = index == 0,
                    isLast = index == sorted.lastIndex
                )
            }
        }
    }
}

@Composable
private fun TimelineRow(
    item: TeamInsightTimelineItem,
    isFirst: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘 원 + 세로 점선 (기록비율과 동일 구조)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp)
            ) {
                val cx = size.width / 2
                val top = if (isFirst) size.height / 2 else 0f
                val bottom = if (isLast) size.height / 2 else size.height
                drawLine(
                    color = DashColor,
                    start = Offset(cx, top),
                    end = Offset(cx, bottom),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f))
                )
            }

            Box(
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, IconCircleBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(ButtonIcons.resOf(item.iconName)),
                    contentDescription = item.buttonName,
                    tint = IconColor.from(item.iconColor).color,
                    modifier = Modifier.size(24.dp)   // ⚠ 시안 아이콘 크기로 조정
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // 버튼명 + 시각 · 경과
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = item.buttonName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TitleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(1.dp))
            Text(
                text = "${item.tappedAt.toTimeText()} • ${item.tappedAt.toElapsedText()}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = SubColor,
                maxLines = 1
            )
        }

        Spacer(Modifier.width(10.dp))

        // 기록한 유저
        UserAvatar(imageUrl = item.member.profileImageUrl, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = item.member.displayName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = NameColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** ISO 문자열 → "10:43 AM" */
private fun String.toTimeText(): String =
    try {
        LocalDateTime.parse(this).format(TimeFormatter)
    } catch (_: Exception) {
        "-"
    }

/** ISO 문자열 → "3시간 전" (화면 열린 시점 기준 FE 계산) */
private fun String.toElapsedText(): String =
    try {
        val minutes = Duration.between(LocalDateTime.parse(this), LocalDateTime.now()).toMinutes()
        when {
            minutes < 1 -> "방금 전"
            minutes < 60 -> "${minutes}분 전"
            minutes < 60 * 24 -> "${minutes / 60}시간 전"
            else -> "${minutes / (60 * 24)}일 전"
        }
    } catch (_: Exception) {
        ""
    }



@Preview(showBackground = true, heightDp = 340)
@Composable
private fun DailyTimelineSectionPreview() {
    PreviewContainer {
        DailyTimelineSection(
            timeline = listOf(
                TeamInsightTimelineItem(
                    teamButtonId = 6L,
                    buttonName = "기획서 업데이트",
                    iconName = "document",
                    iconColor = "#FFC107",
                    tappedAt = LocalDateTime.now().minusHours(3).toString(),
                    member = TeamInsightMember(2L, "누리", null)
                ),
                TeamInsightTimelineItem(
                    teamButtonId = 1L,
                    buttonName = "프론트 코드 수정",
                    iconName = "code",
                    iconColor = "#4C8DFF",
                    tappedAt = LocalDateTime.now().minusHours(5).toString(),
                    member = TeamInsightMember(3L, "하연", null)
                ),
                TeamInsightTimelineItem(
                    teamButtonId = 7L,
                    buttonName = "피그마 업데이트",
                    iconName = "pencil",
                    iconColor = "#FF5C5C",
                    tappedAt = LocalDateTime.now().minusHours(9).toString(),
                    member = TeamInsightMember(4L, "희경", null)
                ),
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "빈 상태", heightDp = 160)
@Composable
private fun DailyTimelineSectionEmptyPreview() {
    PreviewContainer {
        DailyTimelineSection(timeline = emptyList(), modifier = Modifier.padding(16.dp))
    }
}