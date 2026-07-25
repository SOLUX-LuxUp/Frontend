package com.solux.luxup.taptap.feature.team.presentation.insight.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberActivity
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberTopButton
import com.solux.luxup.taptap.core.ui.theme.IconColor
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
// ⚠ 임시 색 — 마지막에 Color.kt 토큰으로 교체
private val TitleColor = Color(0xFF6D6D6D)
private val NameColor = Color(0xFF6D6D6D)    // 시안 natural 70
private val SubColor = Color(0xFF8A94A6)
private val CardBorder = Color(0xFFEDEDED)   // ⚠ 시안 테두리색으로 조정
private val IconCircleBorder = Color(0xFFECEEF1)

@Composable
fun InsightMemberTopButtonSection(
    memberActivity: List<TeamInsightMemberActivity>,
    currentUserId: Long,
    modifier: Modifier = Modifier
) {
    SectionCard(
        modifier = modifier,
        borderColor = CardBorder,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Text(
            text = "가장 많이 기록한 버튼",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TitleColor
        )
        Spacer(Modifier.height(16.dp))

        if (memberActivity.isEmpty()) {
            Text("아직 오늘 기록이 없어요", fontSize = 14.sp, color = SubColor)
        } else {
            // 순위 아님 — 멤버 순서 그대로 나열
            memberActivity.forEachIndexed { index, member ->
                MemberTopButtonRow(
                    member = member,
                    isMe = member.userId == currentUserId
                )
                if (index != memberActivity.lastIndex) Spacer(Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun MemberTopButtonRow(
    member: TeamInsightMemberActivity,
    isMe: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 팀원 프로필
        UserAvatar(imageUrl = member.profileImageUrl, modifier = Modifier.size(24.dp))

        Spacer(Modifier.width(10.dp))

        // 이름 (본인이면 "(나)")
        Text(
            text = if (isMe) "${member.displayName} (나)" else member.displayName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NameColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(56.dp) // ⚠ 시안 이름 폭으로 조정
        )

        Spacer(Modifier.width(12.dp))

        // 그날 최다 버튼 (아이콘 + 이름)
        val top = member.topButton
        if (top != null) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, IconCircleBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(ButtonIcons.resOf(top.iconName)),
                    contentDescription = null,
                    tint = IconColor.from(top.iconColor).color,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = top.buttonName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NameColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "${top.tapCount}회",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NameColor,
                textAlign = TextAlign.End
            )
        } else {
            Text(
                text = "기록 없음",
                fontSize = 14.sp,
                color = SubColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Preview(showBackground = true, heightDp = 320)
@Composable
private fun InsightMemberTopButtonSectionPreview() {
    PreviewContainer {
        InsightMemberTopButtonSection(
            memberActivity = listOf(
                TeamInsightMemberActivity(
                    userId = 4L, displayName = "누리", profileImageUrl = null, tapCount = 10,
                    topButton = TeamInsightMemberTopButton(6L, "기획서 업데이트", "document", "#FFC107", 5)
                ),
                TeamInsightMemberActivity(
                    userId = 7L, displayName = "수민", profileImageUrl = null, tapCount = 5,
                    topButton = TeamInsightMemberTopButton(1L, "프론트 코드 수정", "code", "#4C8DFF", 3)
                ),
                TeamInsightMemberActivity(
                    userId = 9L, displayName = "정민", profileImageUrl = null, tapCount = 3,
                    topButton = TeamInsightMemberTopButton(7L, "피그마 업데이트", "pencil", "#FF5C5C", 2)
                ),
            ),
            currentUserId = 4L, // ⚠ 하드코딩 — 정수민 인증 연동 후 실제 값으로
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "빈 상태", heightDp = 160)
@Composable
private fun InsightMemberTopButtonSectionEmptyPreview() {
    PreviewContainer {
        InsightMemberTopButtonSection(
            memberActivity = emptyList(),
            currentUserId = 4L,
            modifier = Modifier.padding(16.dp)
        )
    }
}