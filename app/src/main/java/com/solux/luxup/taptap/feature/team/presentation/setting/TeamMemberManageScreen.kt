package com.solux.luxup.taptap.feature.team.presentation.setting

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.feature.team.data.MockTeamMembers
import com.solux.luxup.taptap.feature.team.model.TeamMember
import com.solux.luxup.taptap.feature.team.model.TeamMemberRole
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingTopBar
import com.solux.luxup.taptap.feature.team.presentation.setting.components.TeamMemberActionModal
import com.solux.luxup.taptap.feature.team.presentation.setting.components.TeamMemberKickConfirmModal
import java.time.LocalDate

private val ScreenPadding = 40.dp

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
            modifier = Modifier.padding(horizontal = ScreenPadding),
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = ScreenPadding),
        ) {
            item { Spacer(Modifier.height(24.dp)) }

            items(members, key = { it.userId }) { member ->
                MemberManageRow(
                    member = member,
                    isMe = member.userId == currentUserId,
                    onMoreClick = { actionTarget = member },
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

@Composable
private fun MemberManageRow(
    member: TeamMember,
    isMe: Boolean,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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

            if (!isMe) {
                MoreDotsIcon(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onMoreClick,
                    ),
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

/** 이름 (나) + 팀장 배지. 배지는 이름 뒤에 붙는다. */
@Composable
internal fun MemberNameLine(
    member: TeamMember,
    isMe: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (isMe) "${member.displayName} (나)" else member.displayName,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A),
        )
        if (member.role == TeamMemberRole.OWNER) {
            Spacer(Modifier.width(6.dp))
            OwnerBadge()
        }
    }
}

/**
 * 팀장 태그.
 * 팀원 목록 화면의 MemberBadge 와 같은 스펙이다.
 * (그쪽이 private 이라 여기서 다시 선언 — 나중에 공통 컴포넌트로 합치면 좋다)
 */
@Composable
internal fun OwnerBadge(
    modifier: Modifier = Modifier,
    text: String = "팀장",
) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF2085FF))
            .padding(horizontal = 7.dp, vertical = 2.dp),
    )
}

/** ⋯ — 전용 SVG 가 없어 Canvas 로 그린다 */
@Composable
private fun MoreDotsIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFC4C4C4),
) {
    Canvas(modifier.size(24.dp)) {
        val s = size.minDimension
        val r = s * 0.07f
        val gap = s * 0.26f
        listOf(-gap, 0f, gap).forEach { dx ->
            drawCircle(color = tint, radius = r, center = Offset(s / 2f + dx, s / 2f))
        }
    }
}

/** "2026-03-13T00:00:00" → "2026년 3월 13일" */
internal fun formatJoinedDate(iso: String): String = try {
    val date = LocalDate.parse(iso.substring(0, 10))
    "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일"
} catch (e: Exception) {
    "-"
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