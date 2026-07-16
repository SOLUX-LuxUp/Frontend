package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            modifier = Modifier.fillMaxSize(),
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
    }

    // 멤버 초대 모달
    if (showInviteModal) {
        TeamInviteCodeModal(
            inviteCode = inviteCode,
            onDismiss = { showInviteModal = false },
            onShare = { /* TODO: 공유 방식 미정 */ }
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

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5, heightDp = 640)
@Composable
private fun TeamMemberScreenPreview() {
    TeamMemberScreen()
}