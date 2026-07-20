package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.core.util.formatTimeAgo
import com.solux.luxup.taptap.feature.team.model.TeamMember
import com.solux.luxup.taptap.feature.team.model.TeamMemberRole
import com.solux.luxup.taptap.core.ui.theme.Pretendard

@Composable
fun MemberCard(
    member: TeamMember,
    currentUserId: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFEFEFE))
            .border(
                width = 1.dp,
                color = Color(0xFFE2E2E2),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(imageUrl = member.profileImageUrl, size = 44.dp)

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Text(
                    text = member.displayName,
                    fontFamily = Pretendard,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                // "나" 배지 — 내 id와 같을 때
                if (member.userId == currentUserId) {
                    Spacer(Modifier.width(6.dp))
                    MemberBadge(text = "나")
                }
                // "팀장" 배지 — owner일 때
                if (member.role == TeamMemberRole.OWNER) {
                    Spacer(Modifier.width(6.dp))
                    MemberBadge(text = "팀장")
                }
            }
            Spacer(Modifier.height(3.dp))
            androidx.compose.material3.Text(
                text = member.latestRecord
                    ?.let { "최근 기록  ${formatTimeAgo(it.recordedAt)} · ${it.buttonName}" }
                    ?: "최근 기록 없음",
                fontFamily = Pretendard,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D)
            )
        }

        // 오른쪽 chevron (기본 아이콘 세트에 있는 것 → 별도 의존성 불필요)
        androidx.compose.material3.Icon(
            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF6D6D6D),
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
private fun MemberBadge(text: String) {
    androidx.compose.material3.Text(
        text = text,
        fontFamily = Pretendard,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF2085FF))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@androidx.compose.runtime.Composable
private fun MemberCardPreview() {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier.padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
    ) {
        // 누리를 "나"로 지정 → "나" + "팀장" 배지 둘 다 확인
        MemberCard(
            member = TeamMember(
                userId = 1,
                displayName = "누리",
                profileImageUrl = null,
                role = TeamMemberRole.OWNER,
                joinedAt = "2026-03-13T00:00:00",        // ← 추가
                latestRecord = com.solux.luxup.taptap.feature.team.model
                    .MemberLatestRecord("버터 세척", "2025-05-23T14:12:00")
            ),
            currentUserId = 1L,
            onClick = {}
        )
        // 은서 = 내가 아님, 멤버 → 배지 없음
        MemberCard(
            member = TeamMember(
                userId = 2,
                displayName = "은서",
                profileImageUrl = null,
                role = TeamMemberRole.MEMBER,
                joinedAt = "2026-03-19T00:00:00",        // ← 추가
                latestRecord = null
            ),
            currentUserId = 1L,
            onClick = {}
        )
    }
}