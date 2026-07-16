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
import com.solux.luxup.taptap.feature.team.data.MockSharedButtons
import com.solux.luxup.taptap.feature.team.data.MockTeamMemberDetail
import com.solux.luxup.taptap.feature.team.model.TeamMemberDetail
import com.solux.luxup.taptap.feature.team.model.TeamMemberSharedButton
import com.solux.luxup.taptap.feature.team.presentation.memberdetail.components.MemberButtonList
import com.solux.luxup.taptap.feature.team.presentation.memberdetail.components.MemberProfileHeader
import com.solux.luxup.taptap.feature.team.presentation.memberdetail.components.MemberSharedButtonList
import com.solux.luxup.taptap.feature.team.presentation.memberdetail.components.MemberTimelineList

@Composable
fun TeamMemberDetailScreen(
    detail: TeamMemberDetail = MockTeamMemberDetail,
    isMe: Boolean = false,                                              // targetUserId == currentUserId
    sharedButtons: List<TeamMemberSharedButton> = MockSharedButtons,   // 내 프로필 공유 버튼 (8.2.4)
    onEditName: () -> Unit = {},
    onShareSettingsClick: () -> Unit = {},                             // ⚙️ → 공유 설정 모달
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
            editable = isMe,
            onSaveName = { newName -> /* TODO: 8.2.4 PATCH */ }   // onEditName → onSaveName
        )

        // 최근 기록 섹션
        MemberTimelineList(records = detail.recentTimeline)

        // 버튼 섹션 — 나/남 분기
        if (isMe) {
            // 내 프로필: 공유 중인 버튼 (⚙️로 설정 모달)
            MemberSharedButtonList(
                buttons = sharedButtons,
            )
        } else {
            // 남 프로필: 버튼 목록
            MemberButtonList(buttons = detail.buttons)
        }

        Spacer(Modifier.height(20.dp))     // 하단 여백
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 900)
@Composable
private fun TeamMemberDetailScreenPreview() {
    TeamMemberDetailScreen()                 // 남 프로필 (isMe = false)
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 900)
@Composable
private fun TeamMemberDetailScreenMePreview() {
    TeamMemberDetailScreen(isMe = true)      // 내 프로필 (공유 버튼 섹션 + 연필)
}