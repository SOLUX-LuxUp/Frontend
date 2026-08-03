package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.feature.team.data.MockTeamMembers
import com.solux.luxup.taptap.feature.team.model.TeamMember
import com.solux.luxup.taptap.feature.team.presentation.components.MemberCard
import com.solux.luxup.taptap.feature.team.presentation.components.TeamInviteCodeModal

@Composable
fun TeamMemberScreen(
    members: List<TeamMember> = MockTeamMembers,
    currentUserId: Long = 4L,
    inviteCode: String = "SE4EDI",          // 초대코드 (목데이터, API 연결 시 8.0.4)
    onMemberClick: (TeamMember) -> Unit = {},
    onShare: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showInviteModal by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // 헤더: "멤버" 라벨 + 멤버 초대 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 16.dp),   // 좌우 40 통일
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "멤버",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D)
            )
            Spacer(Modifier.weight(1f))
            InviteButton(onClick = { showInviteModal = true })   // 모달 열기
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(members, key = { it.userId }) { member ->
                MemberCard(
                    member = member,
                    currentUserId = currentUserId,
                    onClick = { onMemberClick(member) }
                )
            }
        }
        if (members.none { it.userId != currentUserId }) {
            InviteEmptyState(
                onClick = { showInviteModal = true },
                modifier = Modifier.weight(1f)
            )
        }
    }

    // 멤버 초대 모달
    if (showInviteModal) {
        TeamInviteCodeModal(
            inviteCode = inviteCode,
            onDismiss = { showInviteModal = false },
            onShare = onShare
        )
    }
}

@Composable
private fun InviteButton(onClick: () -> Unit) {
    Text(
        text = "+ 멤버 초대",
        fontSize = 14.sp,
        color = Color(0xFFB1B1B1),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color(0xFFD8D8D8), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
private fun InviteEmptyState(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "친구를 초대해보세요",
            fontFamily = Pretendard,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D)
        )
        Spacer(Modifier.height(20.dp))
        // 원 포함 SVG → Box 배경 없이 아이콘만
        Icon(
            painter = painterResource(R.drawable.ic_invite_friend),   // 실제 파일명으로
            contentDescription = "친구 초대",
            tint = Color.Unspecified,          // 색 포함 SVG → 원본 유지
            modifier = Modifier
                .size(140.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5, heightDp = 640)
@Composable
private fun TeamMemberScreenPreview() {
    TeamMemberScreen()
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 640)
@Composable
private fun TeamMemberScreenEmptyPreview() {
    TeamMemberScreen(members = MockTeamMembers.take(1), currentUserId = 1L)
}