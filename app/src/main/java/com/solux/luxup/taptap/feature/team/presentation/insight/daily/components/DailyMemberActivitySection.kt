package com.solux.luxup.taptap.feature.team.presentation.insight.daily.components

import android.R
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberActivity
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberTopButton

// ⚠ 임시 색 — 마지막에 Color.kt 토큰으로 교체
private val TitleColor = Color(0xFF6D6D6D)
private val NameColor = Color(0xFF6D6D6D)   // 시안 natural 70
private val SubColor = Color(0xFF8A94A6)
private val CardBorder = Color(0xFFEDEDED)  // ⚠ 시안 테두리색으로 조정
private val EtcColor = Color(0xFFD9DEE5)    // 기타 조각 색

/** 도넛에 개별 표시할 최대 인원 (초과분은 "기타"로 묶임) */
private const val MAX_VISIBLE_MEMBERS = 5

// 도넛 조각 팔레트 — 시안 파란 계열(진한→연한). 최대 5명 + 기타(회색)
private val DonutPalette = listOf(
    Color(0xFF0059C5),
    Color(0xFF2085FF),
    Color(0xFF0099FF),
    Color(0xFF4BB4FF),
    Color(0xFF7CCBFF),
)

/** 범례/조각 표시 단위 (개별 멤버 또는 "기타" 묶음) */
private data class ActivitySlice(
    val name: String,
    val tapCount: Int,
    val color: Color,
    val imageUrl: String?,
    val isEtc: Boolean
)

@Composable
fun DailyMemberActivitySection(
    memberActivity: List<TeamInsightMemberActivity>,
    currentUserId: Long,
    modifier: Modifier = Modifier
) {
    val sorted = memberActivity.sortedByDescending { it.tapCount }
    val total = sorted.sumOf { it.tapCount }

    // 상위 5명 개별 표시 + 나머지는 "기타"로 합산
    val slices = buildList {
        sorted.take(MAX_VISIBLE_MEMBERS).forEachIndexed { index, member ->
            add(
                ActivitySlice(
                    name = if (member.userId == currentUserId) {
                        "${member.displayName} (나)"
                    } else {
                        member.displayName
                    },
                    tapCount = member.tapCount,
                    color = DonutPalette[index % DonutPalette.size],
                    imageUrl = member.profileImageUrl,
                    isEtc = false
                )
            )
        }
        val rest = sorted.drop(MAX_VISIBLE_MEMBERS)
        if (rest.isNotEmpty()) {
            add(
                ActivitySlice(
                    name = "기타 ${rest.size}명",
                    tapCount = rest.sumOf { it.tapCount },
                    color = Color(0xFFDEEFFF),
                    imageUrl = null,
                    isEtc = true
                )
            )
        }
    }

    SectionCard(
        modifier = modifier,
        borderColor = CardBorder,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Text(
            text = "팀별 활동량 비율",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TitleColor
        )
        Spacer(Modifier.height(18.dp))

        if (slices.isEmpty() || total <= 0) {
            Text("아직 오늘 기록이 없어요", fontSize = 14.sp, color = SubColor)
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DonutChart(
                    slices = slices,
                    total = total,
                    diameter = 130.dp,   // ⚠ 시안 지름으로 조정
                    ringWidth = 30.dp    // ⚠ 시안 링 두께로 조정
                )

                Spacer(Modifier.width(20.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    slices.forEach { slice ->
                        LegendRow(slice = slice)
                    }
                }
            }
        }
    }
}

@Composable
private fun DonutChart(
    slices: List<ActivitySlice>,
    total: Int,
    diameter: Dp,
    ringWidth: Dp
) {
    val sweepProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 700),
        label = "donutSweep"
    )

    Box(
        modifier = Modifier.size(diameter),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(diameter)) {
            val strokePx = ringWidth.toPx()
            val inset = strokePx / 2
            val arcSize = Size(size.width - strokePx, size.height - strokePx)
            val topLeft = Offset(inset, inset)

            var startAngle = -90f // 12시 방향 시작
            slices.forEach { slice ->
                val fullSweep = slice.tapCount.toFloat() / total * 360f
                drawArc(
                    color = slice.color,
                    startAngle = startAngle,
                    sweepAngle = fullSweep * sweepProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Butt)
                )
                startAngle += fullSweep
            }
        }

        // 중앙: "총" + "N회" (숫자만 파랑)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "총",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NameColor
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$total",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0099ff)   // 숫자만 파랑
                )
                Text(
                    text = "회",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TitleColor
                )
            }
        }
    }
}

@Composable
private fun LegendRow(slice: ActivitySlice) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (slice.isEtc) {
            // 기타: 아바타 대신 조각 색 원
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(slice.color)
            )
        } else {
            UserAvatar(imageUrl = slice.imageUrl, modifier = Modifier.size(24.dp))
        }

        Spacer(Modifier.width(10.dp))

        Text(
            text = slice.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NameColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "${slice.tapCount}회",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NameColor,
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true, heightDp = 340)
@Composable
private fun DailyMemberActivitySectionPreview() {
    PreviewContainer {
        DailyMemberActivitySection(
            memberActivity = listOf(
                TeamInsightMemberActivity(
                    userId = 4L, displayName = "누리", profileImageUrl = null, tapCount = 10,
                    topButton = TeamInsightMemberTopButton(1L, "기획서 업데이트", 5, "doc", "#FFC94C")
                ),
                TeamInsightMemberActivity(
                    userId = 7L, displayName = "수민", profileImageUrl = null, tapCount = 5,
                    topButton = TeamInsightMemberTopButton(2L, "프론트 코드 수정", 3, "code", "#4C8DFF")
                ),
                TeamInsightMemberActivity(
                    userId = 9L, displayName = "정민", profileImageUrl = null, tapCount = 3,
                    topButton = TeamInsightMemberTopButton(3L, "피그마 업데이트", 2, "figma", "#FF6B6B")
                ),
                TeamInsightMemberActivity(
                    userId = 2L, displayName = "은서", profileImageUrl = null, tapCount = 2,
                    topButton = TeamInsightMemberTopButton(4L, "톡방에 연락", 1, "chat", "#7C5CFF")
                ),
            ),
            currentUserId = 4L, // ⚠ 하드코딩 — 정수민 인증 연동 후 실제 값으로
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "6명 초과 (기타 묶음)", heightDp = 380)
@Composable
private fun DailyMemberActivitySectionEtcPreview() {
    PreviewContainer {
        DailyMemberActivitySection(
            memberActivity = listOf(
                TeamInsightMemberActivity(4L, "누리", null, 10, null),
                TeamInsightMemberActivity(7L, "수민", null, 8, null),
                TeamInsightMemberActivity(9L, "정민", null, 6, null),
                TeamInsightMemberActivity(2L, "은서", null, 5, null),
                TeamInsightMemberActivity(3L, "희경", null, 4, null),
                TeamInsightMemberActivity(5L, "하연", null, 3, null),
                TeamInsightMemberActivity(6L, "지우", null, 2, null),
            ),
            currentUserId = 4L,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "빈 상태", heightDp = 160)
@Composable
private fun DailyMemberActivitySectionEmptyPreview() {
    PreviewContainer {
        DailyMemberActivitySection(
            memberActivity = emptyList(),
            currentUserId = 4L,
            modifier = Modifier.padding(16.dp)
        )
    }
}