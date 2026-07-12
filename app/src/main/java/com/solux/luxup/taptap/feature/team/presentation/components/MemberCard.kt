package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun MemberCard(
    member: TeamMember,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .figmaDropShadow(cornerRadius = 14.dp)   // ⚠️ 버튼 카드에서 쓰던 인자 그대로 (아래 설명)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(imageUrl = member.profileImageUrl, size = 44.dp)

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Text(
                    text = member.displayName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                if (member.role == TeamMemberRole.OWNER) {
                    Spacer(Modifier.width(6.dp))
                    OwnerBadge()
                }
            }
            Spacer(Modifier.height(3.dp))
            androidx.compose.material3.Text(
                text = member.latestRecord
                    ?.let { "최근 기록  ${formatTimeAgo(it.recordedAt)} · ${it.buttonName}" }
                    ?: "최근 기록 없음",
                fontSize = 12.sp,
                color = Color(0xFFB1B1B1)
            )
        }

        // 오른쪽 chevron (기본 아이콘 세트에 있는 것 → 별도 의존성 불필요)
        androidx.compose.material3.Icon(
            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFC4C4C4)
        )
    }
}

@Composable
private fun OwnerBadge() {
    androidx.compose.material3.Text(
        text = "리더",
        fontSize = 10.sp,
        color = Color.White,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF2085FF))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@androidx.compose.runtime.Composable
private fun MemberCardPreview() {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier.padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
    ) {
        // 오너 + 최근 기록 있음
        MemberCard(
            member = TeamMember(
                userId = 1,
                displayName = "누리",
                profileImageUrl = null,
                role = TeamMemberRole.OWNER,
                latestRecord = com.solux.luxup.taptap.feature.team.model
                    .MemberLatestRecord("버터 세척", "2025-05-23T14:12:00")
            ),
            onClick = {}
        )
        // 멤버 + 기록 없음
        MemberCard(
            member = TeamMember(
                userId = 2,
                displayName = "은서",
                profileImageUrl = null,
                role = TeamMemberRole.MEMBER,
                latestRecord = null
            ),
            onClick = {}
        )
    }
}