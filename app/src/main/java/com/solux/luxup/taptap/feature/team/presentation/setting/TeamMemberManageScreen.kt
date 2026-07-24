package com.solux.luxup.taptap.feature.team.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.feature.team.data.MockTeamMembers
import com.solux.luxup.taptap.feature.team.model.TeamMember
import com.solux.luxup.taptap.feature.team.presentation.setting.components.MemberListRow
import com.solux.luxup.taptap.feature.team.presentation.setting.components.MoreDotsIcon
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingSpec
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingTopBar
import com.solux.luxup.taptap.feature.team.presentation.setting.components.TeamMemberActionModal
import com.solux.luxup.taptap.feature.team.presentation.setting.components.TeamMemberKickConfirmModal

/**
 * 팀원 관리 화면. 팀 관리 → 팀원 관리 로 진입하며 팀장만 접근한다.
 *
 * 내 카드에는 ⋯ 이 없다 — 본인 강제 추방은 서버가 400 으로 막고,
 * 스스로 나가는 것은 팀 설정의 "팀 나가기"(DELETE /leave)를 쓴다.
 */
@Composable
fun TeamMemberManageScreen(
    members: List<TeamMember>,
    currentUserId: Long,
    onBack: () -> Unit,
    onKickMember: (TeamMember) -> Unit,
    modifier: Modifier = Modifier,
) {
    // ⋯ 을 누른 대상 → 액션 모달
    var actionTarget by remember { mutableStateOf<TeamMember?>(null) }
    // 내보내기를 고른 대상 → 확인 모달
    var kickTarget by remember { mutableStateOf<TeamMember?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        SettingTopBar(
            title = "팀원 관리",
            onBack = onBack,
            modifier = Modifier.padding(horizontal = SettingSpec.ScreenPadding),
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = SettingSpec.ScreenPadding),
        ) {
            item { Spacer(Modifier.height(40.dp)) }

            items(members, key = { it.userId }) { member ->
                val isMe = member.userId == currentUserId
                MemberListRow(
                    member = member,
                    isMe = isMe,
                    trailing = if (isMe) {
                        null
                    } else {
                        {
                            MoreDotsIcon(
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { actionTarget = member },
                            )
                        }
                    },
                )
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }

    actionTarget?.let { target ->
        TeamMemberActionModal(
            onKick = {
                actionTarget = null
                kickTarget = target
            },
            onDismiss = { actionTarget = null },
        )
    }

    kickTarget?.let { target ->
        TeamMemberKickConfirmModal(
            memberName = target.displayName,
            avatar = { UserAvatar(imageUrl = target.profileImageUrl, size = 28.dp) },
            onConfirm = {
                kickTarget = null
                onKickMember(target)
            },
            onDismiss = { kickTarget = null },
        )
    }
}

@Preview(showBackground = true, name = "팀원 관리")
@Composable
private fun TeamMemberManageScreenPreview() {
    PreviewContainer {
        TeamMemberManageScreen(
            members = MockTeamMembers,
            currentUserId = 1L,
            onBack = {},
            onKickMember = {},
        )
    }
}