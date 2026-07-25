package com.solux.luxup.taptap.feature.team.presentation.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.core.ui.components.CheckMarkIcon
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.util.UserAvatar

private val AccentRed = Color(0xFFF6989C)
private val NeutralGrey = Color(0xFFB1B1B1)

/**
 * 팀 스페이스의 "정말 ~할까요?" 확인 모달.
 *
 * 팀 나가기 · 멤버 내보내기 · 팀장 위임이 같은 틀을 쓰고 문구만 다르다.
 * 팀 나가기만 대상이 팀명이라 아바타가 없고, 나머지는 [highlightLeading] 으로
 * 멤버 아바타가 이름 앞에 붙는다.
 *
 * 주의사항 동의 체크를 해야 확인(왼쪽 붉은) 버튼이 눌린다.
 */
@Composable
fun TeamConfirmModal(
    title: String,
    highlight: String,
    description: String,
    agreementText: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    highlightLeading: (@Composable () -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamConfirmModalContent(
            title = title,
            highlight = highlight,
            description = description,
            agreementText = agreementText,
            confirmText = confirmText,
            highlightLeading = highlightLeading,
            onConfirm = onConfirm,
            onCancel = onDismiss,
        )
    }
}

/** Dialog 는 preview 가 불가하므로 내용만 분리 */
@Composable
fun TeamConfirmModalContent(
    title: String,
    highlight: String,
    description: String,
    agreementText: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    highlightLeading: (@Composable () -> Unit)? = null,
) {
    var agreed by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .width(268.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(40.dp))

        Text(
            text = title,
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
        )

        Spacer(Modifier.height(17.dp))

        // 대상 (팀명 / 멤버 이름) 칸 — 204 x 52, radius 100
        Row(
            modifier = Modifier
                .width(204.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Color(0xFFFEFEFE)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (highlightLeading != null) {
                highlightLeading()
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = highlight,
                fontSize = 25.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralGrey,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(31.dp))

        Text(
            text = description,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .width(204.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { agreed = !agreed },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (agreed) AccentRed else Color.White)
                    .border(1.dp, AccentRed, RoundedCornerShape(2.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (agreed) CheckMarkIcon(modifier = Modifier.size(9.dp))
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = agreementText,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
            )
        }

        Spacer(Modifier.height(21.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ConfirmPillButton(
                label = confirmText,
                contentColor = AccentRed,
                enabled = agreed,
                onClick = onConfirm,
            )
            ConfirmPillButton(
                label = "취소",
                contentColor = NeutralGrey,
                enabled = true,
                onClick = onCancel,
            )
        }
        Spacer(Modifier.height(20.dp))
    }
}

/** 99 x 43, radius 100, 테두리 버튼 */
@Composable
private fun ConfirmPillButton(
    label: String,
    contentColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = if (enabled) contentColor else contentColor.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .size(width = 99.dp, height = 43.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(Color.White)
            .border(1.dp, color, RoundedCornerShape(100.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
            color = color,
        )
    }
}

// ── 사용처별 래퍼 ────────────────────────────────────────

/** 팀 나가기 — DELETE /api/teams/{team_id}/leave. 팀명이라 아바타 없음 */
@Composable
fun TeamLeaveConfirmModal(
    teamName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    TeamConfirmModal(
        title = "정말 팀을 나갈까요?",
        highlight = teamName,
        description = "다시 팀에 가입할 수 있습니다\n나의 팀 기록은 자동으로 삭제되지 않습니다",
        agreementText = "주의사항을 확인했으며, 팀을 탈퇴합니다",
        confirmText = "나가기",
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

/** 멤버 내보내기 — DELETE /api/teams/{team_id}/members/{user_id} */
@Composable
fun TeamMemberKickConfirmModal(
    memberName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    avatar: (@Composable () -> Unit)? = null,
) {
    TeamConfirmModal(
        title = "정말 멤버를 내보낼까요?",
        highlight = memberName,
        description = "위 멤버는 다시 팀에 가입할 수 있습니다\n멤버의 팀 기록은 자동으로 삭제되지 않습니다",
        agreementText = "주의사항을 확인했으며, 멤버를 팀에서 내보냅니다",
        confirmText = "확인",
        highlightLeading = avatar,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

/** 팀장 위임 — PATCH /settings 의 newOwnerUserId */
@Composable
fun TeamOwnerDelegateConfirmModal(
    memberName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    avatar: (@Composable () -> Unit)? = null,
) {
    TeamConfirmModal(
        title = "정말 팀장을 위임할까요?",
        highlight = memberName,
        description = "위 멤버에게 팀장 권한이 위임됩니다\n이 작업은 되돌릴 수 없습니다",
        agreementText = "주의사항을 확인했으며, 팀장을 위임합니다",
        confirmText = "확인",
        highlightLeading = avatar,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF666666, name = "팀 나가기")
@Composable
private fun TeamLeaveModalPreview() {
    PreviewContainer {
        TeamConfirmModalContent(
            title = "정말 팀을 나갈까요?",
            highlight = "LUX-UP",
            description = "다시 팀에 가입할 수 있습니다\n나의 팀 기록은 자동으로 삭제되지 않습니다",
            agreementText = "주의사항을 확인했으며, 팀을 탈퇴합니다",
            confirmText = "나가기",
            onConfirm = {},
            onCancel = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF666666, name = "멤버 내보내기")
@Composable
private fun TeamKickModalPreview() {
    PreviewContainer {
        TeamConfirmModalContent(
            title = "정말 멤버를 내보낼까요?",
            highlight = "하연",
            description = "위 멤버는 다시 팀에 가입할 수 있습니다\n멤버의 팀 기록은 자동으로 삭제되지 않습니다",
            agreementText = "주의사항을 확인했으며, 멤버를 팀에서 내보냅니다",
            confirmText = "확인",
            highlightLeading = { UserAvatar(imageUrl = null, size = 30.dp) },
            onConfirm = {},
            onCancel = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF666666, name = "팀장 위임")
@Composable
private fun TeamDelegateModalPreview() {
    PreviewContainer {
        TeamConfirmModalContent(
            title = "정말 팀장을 위임할까요?",
            highlight = "하연",
            description = "위 멤버에게 팀장 권한이 위임됩니다\n이 작업은 되돌릴 수 없습니다",
            agreementText = "주의사항을 확인했으며, 팀장을 위임합니다",
            confirmText = "확인",
            highlightLeading = { UserAvatar(imageUrl = null, size = 30.dp) },
            onConfirm = {},
            onCancel = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}