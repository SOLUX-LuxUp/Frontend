package com.solux.luxup.taptap.feature.team.presentation.memberdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solux.luxup.taptap.feature.team.data.MockTeamMemberDetail
import com.solux.luxup.taptap.feature.team.model.TeamMemberDetail
import com.solux.luxup.taptap.feature.team.presentation.memberdetail.components.MemberButtonList
import com.solux.luxup.taptap.feature.team.presentation.memberdetail.components.MemberProfileHeader
import com.solux.luxup.taptap.feature.team.presentation.memberdetail.components.MemberTimelineList

@Composable
fun TeamMemberDetailScreen(
    detail: TeamMemberDetail = MockTeamMemberDetail,
    isMe: Boolean = false,                 // targetUserId == currentUserId
    onEditName: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 40.dp),   // 좌우 40 통일
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(4.dp))

        // 프로필 헤더 (아바타 + 이름칸 + 최근기록 카드)
        MemberProfileHeader(
            detail = detail,
            editable = isMe,               // 내 프로필이면 연필
            onEditName = onEditName
        )

        // 최근 기록 섹션
        MemberTimelineList(records = detail.recentTimeline)

        // 버튼 목록 섹션
        MemberButtonList(buttons = detail.buttons)

        Spacer(Modifier.height(20.dp))     // 하단 여백
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 900)
@Composable
private fun TeamMemberDetailScreenPreview() {
    TeamMemberDetailScreen()
}