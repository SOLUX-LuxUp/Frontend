package com.solux.luxup.taptap.feature.team.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.ConfirmCheckIcon
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.feature.team.data.MockTeamMembers
import com.solux.luxup.taptap.feature.team.model.TeamMember
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingTopBar
import com.solux.luxup.taptap.feature.team.presentation.setting.components.TeamOwnerDelegateConfirmModal

private val ScreenPadding = 40.dp

/**
 * 팀장 위임 화면. 팀 관리 → 팀장 위임 으로 진입하며 팀장만 접근한다.
 *
 * 멤버를 골라 선택 상태로 만든 뒤 우상단 확인을 눌러야 모달이 뜬다.
 * 내 카드는 고를 수 없다 (자기 자신에게 위임 불가).
 *
 * 실행은 PATCH /api/teams/{team_id}/settings 의 newOwnerUserId.
 * 성공하면 나는 즉시 일반 멤버가 되므로 호출부에서 팀 관리 화면을 빠져나가야 한다.
 */
@Composable
fun TeamOwnerDelegateScreen(
    members: List<TeamMember>,
    currentUserId: Long,
    onBack: () -> Unit,
    onDelegate: (TeamMember) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedUserId by remember { mutableStateOf<Long?>(null) }
    var showConfirm by remember { mutableStateOf(false) }

    val selectedMember = members.firstOrNull { it.userId == selectedUserId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        SettingTopBar(
            title = "팀장 위임",
            onBack = onBack,
            modifier = Modifier.padding(horizontal = ScreenPadding),
            trailing = {
                ConfirmCheckIcon(
                    enabled = selectedMember != null,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = selectedMember != null,
                    ) { showConfirm = true },
                )
            },
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = ScreenPadding),
        ) {
            item { Spacer(Modifier.height(24.dp)) }

            items(members, key = { it.userId }) { member ->
                val isMe = member.userId == currentUserId
                DelegateMemberRow(
                    member = member,
                    isMe = isMe,
                    selected = member.userId == selectedUserId,
                    onClick = if (isMe) null else { { selectedUserId = member.userId } },
                )
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }

    if (showConfirm && selectedMember != null) {
        TeamOwnerDelegateConfirmModal(
            memberName = selectedMember.displayName,
            avatar = { UserAvatar(imageUrl = selectedMember.profileImageUrl, size = 28.dp) },
            onConfirm = {
                showConfirm = false
                onDelegate(selectedMember)
            },
            onDismiss = { showConfirm = false },
        )
    }
}

@Composable
private fun DelegateMemberRow(
    member: TeamMember,
    isMe: Boolean,
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (selected || isMe) Color(0xFFF2F2F2) else Color.Transparent)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    },
                )
                .height(72.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserAvatar(imageUrl = member.profileImageUrl, size = 44.dp)

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                MemberNameLine(member = member, isMe = isMe)

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "가입일  ${formatJoinedDate(member.joinedAt)}",
                    fontSize = 11.sp,
                    color = Color(0xFF9E9E9E),
                )
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFECECEC)),
        )
    }
}

@Preview(showBackground = true, name = "팀장 위임")
@Composable
private fun TeamOwnerDelegateScreenPreview() {
    PreviewContainer {
        TeamOwnerDelegateScreen(
            members = MockTeamMembers,
            currentUserId = 1L,
            onBack = {},
            onDelegate = {},
        )
    }
}